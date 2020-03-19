package com.nelsito.travelplan.actions.mytrips.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.actions.addtrip.AddTripActivity
import com.nelsito.travelplan.actions.detail.view.TripDetailActivity
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.ui.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class TripsListActivity : AppCompatActivity(), CoroutineScope, TripsView, SwipeToDeleteCallback.OnDeleteListener {
    private lateinit var presenter: TripsListPresenter

    companion object {
        private const val NEW_REQ_CODE = 4343
        private const val RC_SIGN_IN = 4346
    }
    private lateinit var listAdapter: TripsListAdapter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        setupBottomBar()

        fab.setOnClickListener { view ->
            startActivityForResult(Intent(this, AddTripActivity::class.java), NEW_REQ_CODE)
        }

        presenter = TripsListPresenter(this, InfraProvider.provideTripRepository())
        job = Job()
        launch {
            progress.visibility = View.VISIBLE
            presenter.loadTrips()
        }
        listAdapter =
            TripsListAdapter(initializePlaces(), clickListener = {
                val intent = Intent(this, TripDetailActivity::class.java)
                intent.putExtra("PlaceId", it.trip.placeId)
                startActivity(intent)
            })
        trip_list.adapter = listAdapter
        val icon: Drawable? = getDrawable(R.drawable.ic_delete_white_24dp)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(icon, listAdapter, this))
        itemTouchHelper.attachToRecyclerView(trip_list)
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
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
            .setTitle("Log Out")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Log Out") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        openLogin()
                    }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
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
        } else if (requestCode == NEW_REQ_CODE) {
            launch {
                progress.visibility = View.VISIBLE
                presenter.loadTrips()
            }
        }
    }

    override fun showTrips(trips: List<TripListItem>) {
        progress.visibility = View.GONE
        if(trips.isNotEmpty()) {
            trip_list.visibility = View.VISIBLE
            empty_placeholder.visibility = View.GONE
            listAdapter.submitList(trips)
        }
    }

    override fun onDeleteTrip(trip: Trip) {
        presenter.onDelete(trip)
    }

    private fun initializePlaces(): PlacesClient {
        val apiKey = getString(R.string.api_key)
        Log.d("Places", "API KEY $apiKey")
        if (apiKey == "") {
            throw Exception("API KEY NOT FOUND")
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Create a new Places client instance
        return Places.createClient(this)
    }
}
