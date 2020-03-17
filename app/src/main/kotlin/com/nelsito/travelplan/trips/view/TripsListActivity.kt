package com.nelsito.travelplan.trips.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.trips.domain.Trip
import com.nelsito.travelplan.ui.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TripsListActivity : AppCompatActivity(), TripsView, SwipeToDeleteCallback.OnDeleteListener {
    private lateinit var presenter: TripsListPresenter

    companion object {
        private const val RC_SIGN_IN = 4346
    }
    private lateinit var listAdapter: TripsListAdapter

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        setupBottomBar()

        presenter = TripsListPresenter(this)
        activityScope.launch {
            presenter.loadTrips()
        }
        listAdapter =
            TripsListAdapter(clickListener = {
                Snackbar.make(trip_list, "Trip selected...", Snackbar.LENGTH_SHORT).show()
            })
        trip_list.adapter = listAdapter
        val icon: Drawable? = getDrawable(R.drawable.ic_delete_white_24dp)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(icon, listAdapter, this))
        itemTouchHelper.attachToRecyclerView(trip_list)

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
                    val user = FirebaseAuth.getInstance().currentUser
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    override fun showTrips(trips: List<TripListItem>) {
        listAdapter.submitList(trips)
    }

    override fun onDeleteTrip(trip: Trip) {
        presenter.onDelete(trip)
    }
}
