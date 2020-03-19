package com.nelsito.travelplan.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.R
import com.nelsito.travelplan.SplashActivity.Companion.RC_SIGN_IN
import com.nelsito.travelplan.mytrips.TripsListActivity
import kotlinx.android.synthetic.main.activity_wait_for_verification.*

class WaitForVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_for_verification)

        lnk_sign_in.setOnClickListener {
            openLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        verify()
    }

    private fun verify() {
        val user = FirebaseAuth.getInstance().currentUser!!
        user.reload().addOnSuccessListener {
            if (user.isEmailVerified) {
                openTrips()
            }
            showUserData(user)
        }.addOnFailureListener {
            Log.e("WaitForVerification", "reload user failed", it)
            showUserData(user)
        }
    }

    private fun showUserData(user: FirebaseUser) {
        txt_username.text = user.displayName
        txt_email.text = user.email
        lnk_verify_email.setOnClickListener {
            user.sendEmailVerification().addOnSuccessListener {
                Snackbar.make(lnk_verify_email, "Email sent...", Snackbar.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.e("Login", "sendEmailVerification exception", it)
                Snackbar.make(lnk_verify_email, "There was an error. Please try again.", Snackbar.LENGTH_SHORT).show()
            }
        }
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
                        user.sendEmailVerification()
                        showUserData(user)
                    } else {
                        openTrips()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Login", "canceled")
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                finish()
            }
        }
    }

    private fun openTrips() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
