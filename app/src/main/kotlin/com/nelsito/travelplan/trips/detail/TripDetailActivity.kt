package com.nelsito.travelplan.trips.detail

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.edit.EditTripActivity
import com.nelsito.travelplan.trips.list.formatDate
import kotlinx.android.synthetic.main.activity_trip_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class TripDetailActivity : AppCompatActivity(), CoroutineScope, OnMapReadyCallback, TripDetailView {
    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
        private const val EDIT_REQ_CODE = 4343
    }

    private lateinit var presenter: TripDetailPresenter
    private lateinit var mMap: GoogleMap

    private lateinit var placesClient: PlacesClient
    private lateinit var listAdapter: PointsOfInterestAdapter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)
        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.menu_edit -> {
                    presenter.editTrip()
                    true
                }
                R.id.menu_delete -> {
                    deleteTrip()
                    true
                }
                else -> false
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        job = Job()
        initializePlaces()
        listAdapter = PointsOfInterestAdapter(placesClient, addPoiClickListener = {
            presenter.addPointOfInterest()
        })
        poi_list.adapter = listAdapter

        val placeId = intent.getStringExtra("PlaceId")
        val user = FirebaseAuth.getInstance().currentUser!!
        presenter = TripDetailPresenter(placeId, user, placesClient, InfraProvider.provideTripRepository())
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun deleteTrip() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Trip")
            .setMessage("Are you sure you want to delete this trip?")
            .setPositiveButton("Delete") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                launch {
                    presenter.deleteTrip()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun showTripInfo(trip: Trip) {
        txt_date.text = trip.formatDate()
        txt_description.text = trip.description
    }

    override fun showPlaceInfo(place: Place) {
        txt_destination_title.text = getString(R.string.trip_to_detail_title, place.name)

        val marker = place.latLng ?: LatLng(0.0, 0.0)
        mMap.addMarker(MarkerOptions().position(marker).title("${place.name}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    override fun showPlacePhotos(bitmap: Bitmap) {
        img_header.setImageBitmap(bitmap)
    }

    override fun tripRemoved() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showTripEdition(trip: Trip) {
        val intent = Intent(this, EditTripActivity::class.java)
        intent.putExtra("Trip", trip)
        startActivityForResult(intent, EDIT_REQ_CODE)
    }

    override fun showError(message: String) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        launch {
            presenter.attachView(this@TripDetailActivity)
        }
    }

    private fun initializePlaces() {
        val apiKey = getString(R.string.api_key)
        Log.d("Places", "API KEY $apiKey")
        if (apiKey == "") {
            Toast.makeText(this, "API KEY NOT FOUND", Toast.LENGTH_LONG).show()
            return
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Create a new Places client instance
        placesClient = Places.createClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showAddPointOfInterest(latLngBounds: LatLngBounds) {
        // Set the fields to specify which types of place data to return after the user has made a selection.
        val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setLocationBias(RectangularBounds.newInstance(latLngBounds))
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun showPointOfInterest(poiSelected: List<PointOfInterestListItem>) {
        listAdapter.submitList(poiSelected)
        poiSelected.forEach {
            if (it is PointOfInterestListItem.PlacePointOfInterestListItem) {
                mMap.addMarker(
                    MarkerOptions().position(it.place.latLng ?: LatLng(0.0, 0.0)).title("${it.place.name}")
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val poiSelected = Autocomplete.getPlaceFromIntent(data)
                    launch {
                        presenter.pointOfInterestAdded(poiSelected)
                    }
                    Log.i("Places", "POI: " + poiSelected.name + ", " + poiSelected.id)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                if (data != null) {
                    val status = Autocomplete.getStatusFromIntent(data);
                    Log.i("Places", status.statusMessage)
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Places", "User Canceled")
            }
        } else if (requestCode == EDIT_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                launch {
                    presenter.refreshTrip()
                }
            }
        }
    }
}
