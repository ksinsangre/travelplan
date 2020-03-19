package com.nelsito.travelplan.actions.detail.view

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.Trip
import kotlinx.android.synthetic.main.activity_trip_detail.*

class TripDetailPresenter(private var trip: Trip, private val placesClient: PlacesClient) {

    private lateinit var place: Place
    private lateinit var tripDetailView: TripDetailView
    private val listOfPOI = mutableListOf<PointOfInterestListItem>(PointOfInterestListItem.FooterPointOfInterestListItem("footer"))

    fun attachView(tripDetailView: TripDetailView) {
        this.tripDetailView = tripDetailView
        tripDetailView.showTripInfo(trip)
        tripDetailView.showPointOfInterest(listOfPOI)
        loadPlace(trip)
    }

    private fun loadPlace(trip: Trip) {
        // Specify the fields to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(trip.placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                this.place = response.place
                tripDetailView.showPlaceInfo(place)
                fetchPhoto(place)
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    val apiException = exception as ApiException
                    val statusCode = apiException.statusCode
                    // Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.message)
                }
            }
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
                tripDetailView.showPlacePhotos(bitmap)
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.message)
                }
            }
        }
    }

    fun addPointOfInterest() {
        tripDetailView.showAddPointOfInterest()
    }

    fun pointOfInterestAdded(poiSelected: Place) {
        listOfPOI.add(listOfPOI.size - 1, PointOfInterestListItem.PlacePointOfInterestListItem(poiSelected))
        tripDetailView.showPointOfInterest(listOfPOI)
    }
}

interface TripDetailView {
    fun showAddPointOfInterest()
    fun showPointOfInterest(poiSelected: List<PointOfInterestListItem>)
    fun showTripInfo(trip: Trip)
    fun showPlaceInfo(place: Place)
    fun showPlacePhotos(bitmap: Bitmap)
}
