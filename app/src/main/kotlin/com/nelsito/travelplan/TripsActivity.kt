package com.nelsito.travelplan

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_trips.*

class TripsActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 4346
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        setupBottomBar()

        val user = FirebaseAuth.getInstance().currentUser
        txt_hello.text = "Hello ${user?.displayName}"
    }

    private fun setupBottomBar() {
        bottomAppBar.replaceMenu(R.menu.menu)
        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    // Do something for menu item 1
                    logout()
                    true
                }
                /*R.id.menu_profile -> {
                    //openCaloriesSettings()
                }
                R.id.menu_filter -> {
                    openFilterPage()
                }
                R.id.menu_clear_filter -> {
                    bottomAppBar.menu.findItem(R.id.menu_clear_filter).isVisible = false
                    loadMeals()
                    true
                }*/
                else -> false
            }
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Goodbye")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        openLogin()
                    }
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
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
            RC_SIGN_IN)
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
                    val user = FirebaseAuth.getInstance().currentUser
                    txt_hello.text = "Hello ${user?.displayName}"
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
