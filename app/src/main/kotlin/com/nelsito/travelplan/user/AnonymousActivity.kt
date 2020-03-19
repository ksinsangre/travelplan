package com.nelsito.travelplan.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.trips.list.TripsListActivity

class AnonymousActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 4346
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openLogin()
    }

    private fun openLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginAppTheme) // Set theme
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
                if (user != null) {
                    //load()
                    if (!user.isEmailVerified) {
                        user.sendEmailVerification().addOnSuccessListener {
                            Log.d("Login", "sendEmailVerification Success")
                        }.addOnFailureListener {
                            Log.e("Login", "sendEmailVerification exception", it)
                        }
                        openWaitingForVerification()
                    } else {
                        openTrips()
                    }
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                finish()
            }
        }
    }


    private fun openWaitingForVerification() {
        val intent = Intent(this, WaitForVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openTrips() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
