package com.nelsito.travelplan.trips.list

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
import com.nelsito.travelplan.R
import com.nelsito.travelplan.trips.add.AddTripActivity
import com.nelsito.travelplan.trips.detail.TripDetailActivity
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import com.nelsito.travelplan.ui.SwipeToDeleteCallback
import com.nelsito.travelplan.user.AnonymousActivity
import com.nelsito.travelplan.user.ProfileActivity

class TripsListActivity : AppCompatActivity(), CoroutineScope, TripsView, SwipeToDeleteCallback.OnDeleteListener {
    private lateinit var presenter: TripsListPresenter

    companion object {
        private const val NEW_REQ_CODE = 4343
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
                startActivityForResult(intent, NEW_REQ_CODE)
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
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                /*R.id.menu_filter -> {
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
                        openAnonymous()
                    }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun openAnonymous() {
        startActivity(Intent(this, AnonymousActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                launch {
                    progress.visibility = View.VISIBLE
                    presenter.loadTrips()
                }
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
