package com.nelsito.travelplan.trips.map

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.MapTripListItemBinding
import com.nelsito.travelplan.databinding.PoiListItemBinding

class MapTripsListAdapter(private var placesClient: PlacesClient, private val clickListener: (MapTripListItem) -> Unit) : ListAdapter<MapTripListItem, RecyclerView.ViewHolder>(MapTripDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingList = MapTripListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TripViewHolder(bindingList, placesClient)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TripViewHolder).bind(getItem(position) as MapTripListItem, clickListener)
    }

    class TripViewHolder(private val binding: MapTripListItemBinding, private val placesClient: PlacesClient) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tripListItem: MapTripListItem, clickListener: (MapTripListItem) -> Unit) {
            binding.txtDestinationTitle.text = tripListItem.destination
            binding.txtPeriod.text = tripListItem.date
            loadPlacePhotos(tripListItem.placeId)
            binding.root.setOnClickListener { clickListener(tripListItem) }
            
            if (tripListItem.daysToGo >= 0) {
                binding.pastOverlay.visibility = View.GONE
                binding.txtDaysToGo.visibility = View.VISIBLE
                binding.txtDaysToGo.text = binding.root.context.resources.getQuantityString(R.plurals.days_to_go, tripListItem.daysToGo, tripListItem.daysToGo)
            } else {
                binding.pastOverlay.visibility = View.VISIBLE
                binding.txtDaysToGo.visibility = View.GONE
            }
        }

        private fun loadPlacePhotos(placeId: String) {

            // Specify the fields to return.
            val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)

            // Construct a request object, passing the place ID and fields array.
            val request = FetchPlaceRequest.builder(placeId, placeFields).build()

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    Log.i("Places", "Place found: " + place.name)
                    Log.i("Places", "Place photos: " + (place.photoMetadatas != null).toString())
                    if(place.photoMetadatas != null) {
                        Log.i("Places", "Place photos: " + place.photoMetadatas!!.size)
                        if (place.photoMetadatas!!.size > 0) {
                            displayPhotoMetadata(place.photoMetadatas!![0], binding.img1)
                        } else {
                            binding.img1.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_image_black_24dp))
                        }
                    }
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        val apiException = exception as ApiException
                        val statusCode = apiException.statusCode
                        // Handle error with given status code.
                        Log.e("Places", "Place not found: " + exception.message)
                    }
                }
        }

        private fun displayPhotoMetadata(photoMetadata: PhotoMetadata, imageView: ImageView) {
            // Get the attribution text.
            val attributions = photoMetadata.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse ->
                    val bitmap: Bitmap = fetchPhotoResponse.bitmap
                    imageView.setImageBitmap(bitmap)
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        val statusCode = exception.statusCode
                        // Handle error with given status code.
                        Log.e("Places", "Place not found: " + exception.message)
                    }
                }
        }
    }

}
