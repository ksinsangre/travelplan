package com.nelsito.travelplan.user.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.net.PlacesClient
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.admin_trip_list_item.view.*
import kotlinx.android.synthetic.main.trip_list_item.view.*
import kotlinx.android.synthetic.main.trip_list_item.view.txt_destination_title
import kotlinx.android.synthetic.main.trip_list_item.view.txt_period

class AdminTripsListAdapter(private val deleteClickListener: (AdminTripListItem) -> Unit) : ListAdapter<AdminTripListItem, RecyclerView.ViewHolder>(AdminTripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TripViewHolder(
            inflater.inflate(
                R.layout.admin_trip_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TripViewHolder).bind(getItem(position) as AdminTripListItem, deleteClickListener)
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tripListItem: AdminTripListItem, clickListener: (AdminTripListItem) -> Unit) {
            itemView.txt_destination_title.text = tripListItem.destination
            itemView.txt_period.text = tripListItem.date
            itemView.btn_delete.setOnClickListener { clickListener(tripListItem) }
        }
    }

}
