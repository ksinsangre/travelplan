package com.nelsito.travelplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 4346
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            openTrips()
        } else {
            openLogin()
        }
    }

    private fun openLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                //.setTheme(R.style.LoginAppTheme) // Set theme
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser!!
            if (!user.isEmailVerified) user.sendEmailVerification()

            openTrips()
        }
    }

    private fun openTrips() {
        val intent = Intent(this@SplashActivity, TripsActivity::class.java)
        startActivity(intent)
        finish()
    }
}