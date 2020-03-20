package com.nelsito.travelplan.trips.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.detail.TripDetailActivity
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.trips.list.TripsListAdapter
import com.nelsito.travelplan.ui.OnSnapPositionChangeListener
import com.nelsito.travelplan.ui.SnapOnScrollListener
import com.nelsito.travelplan.ui.attachSnapHelperWithListener
import kotlinx.android.synthetic.main.activity_edit_trip.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.toolbar
import kotlinx.android.synthetic.main.activity_maps.trip_list
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MapTripsActivity : AppCompatActivity(), OnMapReadyCallback, MapTripsView, CoroutineScope {

    private lateinit var listAdapter: MapTripsListAdapter
    private lateinit var presenter: MapTripsPresenter
    private lateinit var mMap: GoogleMap

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        job = Job()

        presenter = MapTripsPresenter(this, InfraProvider.provideTripRepository())

        val snapHelper = LinearSnapHelper()
        trip_list.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, presenter)

        listAdapter =
            MapTripsListAdapter(initializePlaces(), clickListener = {
                val intent = Intent(this, TripDetailActivity::class.java)
                intent.putExtra("PlaceId", it.placeId)
                startActivity(intent)
            })
        trip_list.adapter = listAdapter

        launch {
            presenter.loadTrips()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        presenter.mapReady()
    }

    override fun mapTrips(trips: List<MapTripListItem>) {
        trips.forEach {
            mMap.addMarker(MarkerOptions().position(it.latLng).title(it.destination))
        }
        listAdapter.submitList(trips)
        if(trips.isNotEmpty()) {
            trip_list.visibility = View.VISIBLE
        } else {
            trip_list.visibility = View.GONE
        }
    }

    override fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}
