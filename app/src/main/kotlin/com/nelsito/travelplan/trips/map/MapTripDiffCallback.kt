package com.nelsito.travelplan.trips.map

import androidx.recyclerview.widget.DiffUtil

class MapTripDiffCallback : DiffUtil.ItemCallback<MapTripListItem>() {
    override fun areItemsTheSame(oldItem: MapTripListItem, newItem: MapTripListItem): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(oldItem: MapTripListItem, newItem: MapTripListItem): Boolean {
        return oldItem.destination == newItem.destination &&
                oldItem.date == newItem.date
    }
}
