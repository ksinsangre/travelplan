package com.nelsito.travelplanadmin.user.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.travelplanadmin.R
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserListAdapter(private val context: Context): ListAdapter<UserListItem, RecyclerView.ViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(
            inflater.inflate(
                R.layout.user_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(getItem(position) as UserListItem, context)
    }

    private class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(userListItem: UserListItem, context: Context) {
            itemView.txt_username.text = userListItem.username
            itemView.txt_email.text = userListItem.email
            Glide.with(context)
                .load(userListItem.photoUrl)
                .centerCrop()
                .placeholder(context.getDrawable(R.drawable.ic_person_white_24dp))
                .into(itemView.img_avatar)
        }
    }
}