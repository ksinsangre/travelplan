package com.nelsito.travelplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 4346
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                //.setTheme(R.style.LoginAppTheme) // Set theme
                .build(),
            RC_SIGN_IN)
    }
}
