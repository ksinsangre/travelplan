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
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityFilterBinding
import com.nelsito.travelplan.databinding.ActivityTripsBinding
import com.nelsito.travelplan.domain.LoadTrips
import com.nelsito.travelplan.domain.LocalDateService
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.trips.add.AddTripActivity
import com.nelsito.travelplan.trips.detail.TripDetailActivity
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.map.MapTripsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import com.nelsito.travelplan.ui.SwipeToDeleteCallback
import com.nelsito.travelplan.user.ProfileActivity
import com.nelsito.travelplan.user.login.LoginActivity

class TripsListActivity : AppCompatActivity(), TripsView, SwipeToDeleteCallback.OnDeleteListener {

    private val viewModel by viewModels<TripListViewModel>()

    companion object {
        private const val NEW_REQ_CODE = 4343
        private const val SEARCH_REQ_CODE = 4344
    }
    private lateinit var listAdapter: TripsListAdapter

    private lateinit var binding: ActivityTripsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBottomBar()

        binding.fab.setOnClickListener { view ->
            startActivityForResult(Intent(this, AddTripActivity::class.java), NEW_REQ_CODE)
        }


        listAdapter = TripsListAdapter(initializePlaces(), viewModel::onTripClicked)
        val icon: Drawable? = AppCompatResources.getDrawable(this, R.drawable.ic_delete_white_24dp)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(icon, listAdapter, this))
        itemTouchHelper.attachToRecyclerView(binding.tripList)
        binding.tripList.adapter = listAdapter

        viewModel.trips.observe(this, this::showTrips)

        viewModel.progress.observe(this) {
            if (it) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }
        }

        viewModel.selectedTrip.observe(this, this::onTripSelected)

        viewModel.loadTrips()
    }

    private fun onTripSelected(item: TripListItem) {
        val intent = Intent(this, TripDetailActivity::class.java)
        intent.putExtra("PlaceId", item.trip.placeId)
        startActivityForResult(intent, NEW_REQ_CODE)
    }

    private fun setupBottomBar() {
        binding.bottomAppBar.replaceMenu(R.menu.menu)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
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
                    binding.bottomAppBar.menu.findItem(R.id.menu_clear_filter).isVisible = false
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
        intent.putExtra("LAST_SEARCH", viewModel.lastSearch)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.loadTrips()
            }
        } else if (requestCode == SEARCH_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.bottomAppBar.menu.findItem(R.id.menu_clear_filter).isVisible = true
                val search = data?.getParcelableExtra<Search>("SEARCH")
                if (search != null) {
                    viewModel.searchTrips(search)
                }
            }
        }
    }

    override fun showTrips(trips: List<TripListItem>) {
        listAdapter.submitList(trips)
        if(trips.isNotEmpty()) {
            binding.bottomAppBar.menu.findItem(R.id.menu_map_trips).isVisible = true
            binding.tripList.visibility = View.VISIBLE
            binding.emptyPlaceholder.visibility = View.GONE
        } else {
            binding.bottomAppBar.menu.findItem(R.id.menu_map_trips).isVisible = false
            binding.tripList.visibility = View.GONE
            binding.emptyPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun onDeleteTrip(trip: Trip) {
        viewModel.delete(trip)
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
