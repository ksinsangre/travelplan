package com.nelsito.travelplan.user.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.AdminTripListItemBinding
import com.nelsito.travelplan.databinding.MapTripListItemBinding

class AdminTripsListAdapter(private val deleteClickListener: (AdminTripListItem) -> Unit) : ListAdapter<AdminTripListItem, RecyclerView.ViewHolder>(AdminTripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingList = AdminTripListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TripViewHolder(bindingList)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TripViewHolder).bind(getItem(position) as AdminTripListItem, deleteClickListener)
    }

    class TripViewHolder(private val binding: AdminTripListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tripListItem: AdminTripListItem, clickListener: (AdminTripListItem) -> Unit) {
            binding.txtDestinationTitle.text = tripListItem.destination
            binding.txtPeriod.text = tripListItem.date
            binding.btnDelete.setOnClickListener { clickListener(tripListItem) }
        }
    }

}
