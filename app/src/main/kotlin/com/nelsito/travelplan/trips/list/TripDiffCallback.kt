package com.nelsito.travelplan.trips.list

import androidx.recyclerview.widget.DiffUtil

class TripDiffCallback : DiffUtil.ItemCallback<TripListItem>() {
    override fun areItemsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.trip.placeId == newItem.trip.placeId
    }

    override fun areContentsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.destination == newItem.destination &&
                oldItem.date == newItem.date
    }
}
