package com.nelsito.travelplan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w("Splash", "getDynamicLink:onFailure", e) }
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