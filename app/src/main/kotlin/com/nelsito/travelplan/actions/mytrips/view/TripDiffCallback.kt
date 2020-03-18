package com.nelsito.travelplan.actions.mytrips.view

import androidx.recyclerview.widget.DiffUtil

class TripDiffCallback : DiffUtil.ItemCallback<TripListItem>() {
    override fun areItemsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.trip.id == newItem.trip.id
    }

    override fun areContentsTheSame(oldItem: TripListItem, newItem: TripListItem): Boolean {
        return oldItem.destination == newItem.destination &&
                oldItem.date == newItem.date
    }
}
