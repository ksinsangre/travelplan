package com.nelsito.travelplan.detail.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.activity_trip_detail.*

class TripDetailActivity : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        initializePlaces()

        val place = intent.getParcelableExtra<Place>("Place")
        txt_destination_title.text = getString(R.string.trip_to_detail_title, place.name)

        fetchPhoto(place)
    }


    private fun fetchPhoto(place: Place) {
        // Get the photo metadata.
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

    private fun initializePlaces() {
        val apiKey = getString(R.string.places_api_key)
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
}
