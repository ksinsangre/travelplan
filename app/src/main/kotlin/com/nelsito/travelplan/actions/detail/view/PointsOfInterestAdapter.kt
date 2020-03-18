package com.nelsito.travelplan.actions.detail.view

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.poi_add_item.view.*
import kotlinx.android.synthetic.main.poi_list_item.view.*

class PointsOfInterestAdapter(private var placesClient: PlacesClient, private val addPoiClickListener: () -> Unit) : ListAdapter<PointOfInterestListItem, RecyclerView.ViewHolder>(
    POIDiffCallback()
) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PointOfInterestListItem.PlacePointOfInterestListItem -> 1
            is PointOfInterestListItem.FooterPointOfInterestListItem -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 1 ) {
            return PoiViewHolder(
                inflater.inflate(
                    R.layout.poi_list_item,
                    parent,
                    false
                ), placesClient
            )
        } else {
            return FooterHolder(
                inflater.inflate(
                    R.layout.poi_add_item,
                    parent,
                    false
                )
            )
        }
    }

    class PoiViewHolder(
        itemView: View,
        private val placesClient: PlacesClient
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PointOfInterestListItem.PlacePointOfInterestListItem) {

            itemView.txt_poi_name.text = item.place.name
            val photoMetadata = item.place.photoMetadatas!![0]

            // Get the attribution text.
            val attributions = photoMetadata.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap: Bitmap = fetchPhotoResponse.bitmap
                itemView.imageView1.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.message)
                }
            }
        }
    }

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(addPoiClickListener: () -> Unit) {
            itemView.fab.setOnClickListener { addPoiClickListener() }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            (holder as FooterHolder).bind(addPoiClickListener)
        } else {
            (holder as PoiViewHolder).bind(getItem(position) as PointOfInterestListItem.PlacePointOfInterestListItem)
        }
    }
}