package com.nelsito.travelplan.user.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.nelsito.travelplan.R
import com.nelsito.travelplan.SplashActivity
import com.nelsito.travelplan.infra.FirebaseUserRepository
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.ui.UserNavigationPresenter
import com.nelsito.travelplan.user.UserNavigationView
import com.nelsito.travelplan.user.list.UserListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BuiltInLoginActivity : AppCompatActivity(), UserNavigationView, CoroutineScope {

    private lateinit var presenter: UserNavigationPresenter
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        presenter = UserNavigationPresenter(this, InfraProvider.provideUserRepository())

        showLoginScreen()
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    private fun showLoginScreen() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginAppTheme) // Set theme
                .build(),
            SplashActivity.RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SplashActivity.RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            launch {
                presenter.startNavigation()
            }
        } else{
            finish()
        }
    }

    override fun openLogin() {
        showLoginScreen()
    }

    override fun openUserList() {
        val intent = Intent(this, UserListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun openWaitForVerificationScreen() {
        val intent = Intent(this, WaitForVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun openTripList() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
