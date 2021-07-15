package com.nelsito.travelplan.trips.list

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.LoadTrips
import com.nelsito.travelplan.domain.LocalDateService
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.trips.add.AddTripActivity
import com.nelsito.travelplan.trips.detail.TripDetailActivity
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.map.MapTripsActivity
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import com.nelsito.travelplan.ui.SwipeToDeleteCallback
import com.nelsito.travelplan.user.ProfileActivity
import com.nelsito.travelplan.user.login.LoginActivity

class TripsListActivity : AppCompatActivity(), CoroutineScope, TripsView, SwipeToDeleteCallback.OnDeleteListener {

    private val viewModel by viewModels<TripListViewModel>()

    companion object {
        private const val NEW_REQ_CODE = 4343
        private const val SEARCH_REQ_CODE = 4344
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


        listAdapter = TripsListAdapter(initializePlaces(), viewModel::onTripClicked)
        val icon: Drawable? = getDrawable(R.drawable.ic_delete_white_24dp)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(icon, listAdapter, this))
        itemTouchHelper.attachToRecyclerView(trip_list)
        trip_list.adapter = listAdapter

        viewModel.trips.observe(this, this::showTrips)

        viewModel.progress.observe(this, {
            if (it) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
            }
        })

        viewModel.selectedTrip.observe(this, this::onTripSelected)

        viewModel.loadTrips()
    }

    private fun onTripSelected(item: TripListItem) {
        val intent = Intent(this, TripDetailActivity::class.java)
        intent.putExtra("PlaceId", item.trip.placeId)
        startActivityForResult(intent, NEW_REQ_CODE)
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
                R.id.menu_map_trips -> {
                    startActivity(Intent(this, MapTripsActivity::class.java))
                    true
                }
                R.id.menu_filter -> {
                    openFilterPage()
                    true
                }
                R.id.menu_clear_filter -> {
                    bottomAppBar.menu.findItem(R.id.menu_clear_filter).isVisible = false
                    viewModel.clearFilter()
                    viewModel.loadTrips()
                    true
                }
                else -> false
            }
        }
    }

    private fun openFilterPage() {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra("LAST_SEARCH", presenter.lastSearch)
        startActivityForResult(intent, SEARCH_REQ_CODE)
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
        startActivity(Intent(this, LoginActivity::class.java))
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
        } else if (requestCode == SEARCH_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                bottomAppBar.menu.findItem(R.id.menu_clear_filter).isVisible = true
                launch {
                    val search = data?.getParcelableExtra<Search>("SEARCH")
                    if (search != null) {
                        progress.visibility = View.VISIBLE
                        presenter.searchTrips(search)
                    }
                }
            }
        }
    }

    override fun showTrips(trips: List<TripListItem>) {
        listAdapter.submitList(trips)
        if(trips.isNotEmpty()) {
            bottomAppBar.menu.findItem(R.id.menu_map_trips).isVisible = true
            trip_list.visibility = View.VISIBLE
            empty_placeholder.visibility = View.GONE
        } else {
            bottomAppBar.menu.findItem(R.id.menu_map_trips).isVisible = false
            trip_list.visibility = View.GONE
            empty_placeholder.visibility = View.VISIBLE
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
