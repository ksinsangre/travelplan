package com.nelsito.travelplan.trips.detail

import android.graphics.Bitmap
import android.util.Log
import androidx.core.util.Pair
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository

class TripDetailPresenter(private var placeId: String,
                          private val placesClient: PlacesClient,
                          private val tripRepository: TripRepository) {

    private lateinit var place: Place
    private lateinit var trip: Trip
    private lateinit var tripDetailView: TripDetailView
    private var listOfPOI = mutableListOf<PointOfInterestListItem>(PointOfInterestListItem.FooterPointOfInterestListItem("footer"))

    fun attachView(tripDetailView: TripDetailView) {
        this.tripDetailView = tripDetailView
        this.trip = tripRepository.find(placeId)
        tripDetailView.showTripInfo(trip)
        tripDetailView.showPointOfInterest(listOfPOI)
        loadPlace(trip)
        loadPointsOfInterest(trip)
    }

    private fun loadPointsOfInterest(trip: Trip) {
        // Specify the fields to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)
        listOfPOI = mutableListOf(PointOfInterestListItem.FooterPointOfInterestListItem("footer"))
        var fetched = 0;
        trip.pointsOfInterest.forEach {
            // Construct a request object, passing the place ID and fields array.
            val request = FetchPlaceRequest.builder(it, placeFields).build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val poi = response.place
                    listOfPOI.add(
                        listOfPOI.size - 1,
                        PointOfInterestListItem.PlacePointOfInterestListItem(poi)
                    )

                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        val apiException = exception as ApiException
                        val statusCode = apiException.statusCode
                        // Handle error with given status code.
                        Log.e("Places", "Place not found: " + exception.message)
                    }
                }
                .addOnCompleteListener {
                    fetched++
                    if (fetched == trip.pointsOfInterest.size) {
                        tripDetailView.showPointOfInterest(listOfPOI)
                    }
                }
        }
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
        val upper = LatLng(place.latLng?.latitude?.plus(0.5) ?: 0.0,
            place.latLng?.longitude?.plus(0.5) ?: 0.0)
        val lower = LatLng(place.latLng?.latitude?.minus(0.5) ?: 0.0,
            place.latLng?.longitude?.minus(0.5) ?: 0.0)

        val latLngBounds = LatLngBounds.builder().include(place.latLng).include(upper).include(lower).build()

        tripDetailView.showAddPointOfInterest(latLngBounds)
    }

    fun pointOfInterestAdded(poiSelected: Place) {
        trip.pointsOfInterest.add(poiSelected.id ?: "")
        tripRepository.update(trip)
    }

    fun dateChanged(dateFrom: Long, dateTo: Long) {
        trip = trip.copy(dateFrom = dateFrom, dateTo = dateTo)
        tripDetailView.showTripInfo(trip)
        tripRepository.update(trip)
    }
}

interface TripDetailView {
    fun showAddPointOfInterest(latLngBounds: LatLngBounds)
    fun showPointOfInterest(poiSelected: List<PointOfInterestListItem>)
    fun showTripInfo(trip: Trip)
    fun showPlaceInfo(place: Place)
    fun showPlacePhotos(bitmap: Bitmap)
}
