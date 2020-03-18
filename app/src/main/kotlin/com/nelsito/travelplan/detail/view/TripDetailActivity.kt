package com.nelsito.travelplan.detail.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
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
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.nelsito.travelplan.R
import com.nelsito.travelplan.mytrips.view.TripsListAdapter
import kotlinx.android.synthetic.main.activity_trip_detail.*


class TripDetailActivity : AppCompatActivity(), OnMapReadyCallback, TripDetailView {
    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private lateinit var presenter: TripDetailPresenter
    private lateinit var place: Place
    private lateinit var mMap: GoogleMap

    private lateinit var placesClient: PlacesClient
    private lateinit var listAdapter: PointsOfInterestAdapter

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initializePlaces()

        place = intent.getParcelableExtra("Place")
        txt_destination_title.text = getString(R.string.trip_to_detail_title, place.name)

        fetchPhoto(place)

        listAdapter = PointsOfInterestAdapter(placesClient, addPoiClickListener = {
            presenter.addPointOfInterest()
        })
        poi_list.adapter = listAdapter

        presenter = TripDetailPresenter()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }


    private fun fetchPhoto(place: Place) {
        // Get the photo metadata.
        if (place.photoMetadatas != null) {
            val photoMetadata = place.photoMetadatas!![0]

            // Get the attribution text.
            val attributions = photoMetadata.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap: Bitmap = fetchPhotoResponse.bitmap
                img_header.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.message)
                }
            }
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

        val marker = place.latLng ?: LatLng(0.0, 0.0)
        mMap.addMarker(MarkerOptions().position(marker).title("${place.name}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showAddPointOfInterest() {
        // Set the fields to specify which types of place data to return after the user has made a selection.
        val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)


        val upper = LatLng(place.latLng?.latitude?.plus(0.5) ?: 0.0,
                            place.latLng?.longitude?.plus(0.5) ?: 0.0)
        val lower = LatLng(place.latLng?.latitude?.minus(0.5) ?: 0.0,
            place.latLng?.longitude?.minus(0.5) ?: 0.0)

        val latLngBounds = LatLngBounds.builder().include(place.latLng).include(upper).include(lower).build()
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val poiSelected = Autocomplete.getPlaceFromIntent(data)
                    presenter.pointOfInterestAdded(poiSelected)
                    mMap.addMarker(MarkerOptions().position(poiSelected.latLng?:LatLng(0.0,0.0)).title("${place.name}"))
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
        }
    }
}
