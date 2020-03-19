package com.nelsito.travelplan.trips.list

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
import kotlinx.android.synthetic.main.trip_list_item.view.*

class TripsListAdapter(private var placesClient: PlacesClient, private val clickListener: (TripListItem) -> Unit) : ListAdapter<TripListItem, RecyclerView.ViewHolder>(TripDiffCallback()) {
    private var recentlyDeletedItemPosition: Int = -1
    private lateinit var recentlyDeletedItem: TripListItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TripViewHolder(
            inflater.inflate(
                R.layout.trip_list_item,
                parent,
                false
            ),
            placesClient
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TripViewHolder).bind(getItem(position) as TripListItem, clickListener)
    }

    fun deleteItem(position: Int) {
        recentlyDeletedItem = getItem(position)
        recentlyDeletedItemPosition = position
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
        notifyItemRemoved(position)
    }

    class TripViewHolder(itemView: View, private val placesClient: PlacesClient) : RecyclerView.ViewHolder(itemView) {
        fun bind(tripListItem: TripListItem, clickListener: (TripListItem) -> Unit) {
            itemView.txt_destination_title.text = tripListItem.destination
            if (tripListItem.description.isEmpty()) {
                itemView.txt_description.visibility = View.GONE
            }
            else {
                itemView.txt_description.visibility = View.VISIBLE
                itemView.txt_description.text = tripListItem.description
            }
            itemView.txt_period.text = tripListItem.date
            loadPlacePhotos(tripListItem.trip.placeId)
            itemView.setOnClickListener { clickListener(tripListItem) }
            
            if (tripListItem.daysToGo >= 0) {
                itemView.past_overlay.visibility = View.GONE
                itemView.txt_days_to_go.visibility = View.VISIBLE
                itemView.txt_days_to_go.text = itemView.context.resources.getQuantityString(R.plurals.days_to_go, tripListItem.daysToGo, tripListItem.daysToGo)
            } else {
                itemView.past_overlay.visibility = View.VISIBLE
                itemView.txt_days_to_go.visibility = View.GONE
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
                            displayPhotoMetadata(place.photoMetadatas!![0], itemView.img_1)
                        } else {
                            itemView.img_1.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_image_black_24dp))
                        }
                        if (place.photoMetadatas!!.size > 1) displayPhotoMetadata(place.photoMetadatas!![1], itemView.img_2)
                        if (place.photoMetadatas!!.size > 2) displayPhotoMetadata(place.photoMetadatas!![2], itemView.img_3)
                        if (place.photoMetadatas!!.size > 3) displayPhotoMetadata(place.photoMetadatas!![3], itemView.img_4)
                        if (place.photoMetadatas!!.size > 4) displayPhotoMetadata(place.photoMetadatas!![4], itemView.img_5)
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
