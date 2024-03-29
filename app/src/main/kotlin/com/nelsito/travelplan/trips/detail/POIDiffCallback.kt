package com.nelsito.travelplan.trips.detail

import androidx.recyclerview.widget.DiffUtil

class POIDiffCallback : DiffUtil.ItemCallback<PointOfInterestListItem>() {
    override fun areItemsTheSame(oldItem: PointOfInterestListItem, newItem: PointOfInterestListItem): Boolean {
        return when(oldItem) {
            is PointOfInterestListItem.PlacePointOfInterestListItem -> newItem is PointOfInterestListItem.PlacePointOfInterestListItem && newItem.place.id == oldItem.place.id
            is PointOfInterestListItem.FooterPointOfInterestListItem -> newItem is PointOfInterestListItem.FooterPointOfInterestListItem
        }
    }

    override fun areContentsTheSame(oldItem: PointOfInterestListItem, newItem: PointOfInterestListItem): Boolean {
        return when(oldItem) {
            is PointOfInterestListItem.PlacePointOfInterestListItem -> newItem is PointOfInterestListItem.PlacePointOfInterestListItem && newItem.place.latLng == oldItem.place.latLng
            is PointOfInterestListItem.FooterPointOfInterestListItem -> newItem is PointOfInterestListItem.FooterPointOfInterestListItem
        }
    }
}
