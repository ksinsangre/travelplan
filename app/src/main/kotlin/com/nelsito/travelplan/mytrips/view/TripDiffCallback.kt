package com.nelsito.travelplan.mytrips.view

import androidx.recyclerview.widget.DiffUtil

class TripDiffCallback : DiffUtil.ItemCallback<TripListItem>() {
    override fun areItemsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.trip.id == newItem.trip.id
    }

    override fun areContentsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.destination == newItem.destination &&
                oldItem.images == newItem.images &&
                oldItem.date == newItem.date
    }
}
