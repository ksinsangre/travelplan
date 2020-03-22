package com.nelsito.travelplan.user.admin

import androidx.recyclerview.widget.DiffUtil

class AdminTripDiffCallback : DiffUtil.ItemCallback<AdminTripListItem>() {
    override fun areItemsTheSame(oldItem: AdminTripListItem, newItem: AdminTripListItem): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(oldItem: AdminTripListItem, newItem: AdminTripListItem): Boolean {
        return oldItem.destination == newItem.destination &&
                oldItem.date == newItem.date
    }
}
