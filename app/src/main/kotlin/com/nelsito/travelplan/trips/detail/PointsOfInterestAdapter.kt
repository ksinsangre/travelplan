package com.nelsito.travelplan.trips.detail

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
import com.nelsito.travelplan.databinding.PoiAddItemBinding
import com.nelsito.travelplan.databinding.PoiListItemBinding

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
        val bindingList = PoiListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val bindingAddItemBinding = PoiAddItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return if (viewType == 1 ) {
            PoiViewHolder(bindingList, placesClient)
        } else {
            FooterHolder(bindingAddItemBinding)
        }
    }

    class PoiViewHolder(
        private val binding: PoiListItemBinding,
        private val placesClient: PlacesClient
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PointOfInterestListItem.PlacePointOfInterestListItem) {

            binding.txtPoiName.text = item.place.name
            val photoMetadata = item.place.photoMetadatas!![0]

            // Get the attribution text.
            val attributions = photoMetadata.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                val bitmap: Bitmap = fetchPhotoResponse.bitmap
                binding.imageView1.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.message)
                }
            }
        }
    }

    class FooterHolder(private val binding: PoiAddItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(addPoiClickListener: () -> Unit) {
            binding.fab.setOnClickListener { addPoiClickListener() }
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