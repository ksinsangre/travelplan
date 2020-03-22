package com.nelsito.travelplan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.user.UserNavigationView
import com.nelsito.travelplan.user.login.WaitForVerificationActivity
import com.nelsito.travelplan.user.list.UserListActivity
import com.nelsito.travelplan.user.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), UserNavigationView, CoroutineScope {

    companion object {
        const val RC_SIGN_IN = 4346
    }
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        val presenter = UserNavigationPresenter(
            this,
            InfraProvider.provideUserRepository()
        )
        launch {
            presenter.startNavigation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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