package com.nelsito.travelplan.mytrips.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.trip_list_item.view.*

class TripsListAdapter(private val clickListener: (TripListItem) -> Unit) : ListAdapter<TripListItem, RecyclerView.ViewHolder>(TripDiffCallback()) {
    private var recentlyDeletedItemPosition: Int = -1
    private lateinit var recentlyDeletedItem: TripListItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TripViewHolder(
            inflater.inflate(
                R.layout.trip_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TripViewHolder).bind(getItem(position) as TripListItem, clickListener)
    }

    fun deleteItem(position: Int) {
        recentlyDeletedItem = getItem(position)
        recentlyDeletedItemPosition = position
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
        notifyItemRemoved(position)
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tripListItem: TripListItem, clickListener: (TripListItem) -> Unit) {
            itemView.txt_destination_title.text = tripListItem.destination
            itemView.txt_description.text = tripListItem.description
            itemView.txt_period.text = tripListItem.date
            loadImage(itemView.img_1 as ImageView, tripListItem.images[0])
            loadImage(itemView.img_2 as ImageView, tripListItem.images[1])
            loadImage(itemView.img_3 as ImageView, tripListItem.images[0])
            loadImage(itemView.img_4 as ImageView, tripListItem.images[1])
            loadImage(itemView.img_5 as ImageView, tripListItem.images[0])
            itemView.setOnClickListener { clickListener(tripListItem) }
            
            if (tripListItem.daysToGo >= 0) {
                itemView.past_overlay.visibility = View.GONE
                itemView.txt_days_to_go.visibility = View.VISIBLE
                itemView.txt_days_to_go.text = itemView.context.resources.getQuantityString(R.plurals.days_to_go, tripListItem.daysToGo, tripListItem.daysToGo)
            } else {
                itemView.past_overlay.visibility = View.VISIBLE
                itemView.txt_days_to_go.visibility = View.GONE
            }
        }

        private fun loadImage(imageView: ImageView, url: String) {
            Glide
                .with(itemView.context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(imageView)
        }
    }

}
