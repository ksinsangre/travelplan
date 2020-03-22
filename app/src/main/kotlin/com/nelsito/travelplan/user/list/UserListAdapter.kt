package com.nelsito.travelplan.user.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserListAdapter(private val context: Context, private val clickListener: (UserListItem) -> Unit): ListAdapter<UserListItem, RecyclerView.ViewHolder>(UserDiffCallback()) {
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
        (holder as UserViewHolder).bind(getItem(position) as UserListItem, context, clickListener)
    }

    private class UserViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(userListItem: UserListItem, context: Context,
                 clickListener: (UserListItem) -> Unit) {
            itemView.txt_username.text = userListItem.username
            itemView.txt_email.text = userListItem.email

            if (userListItem.disabled) {
                itemView.lbl_manager.visibility = View.GONE
                itemView.lbl_admin.visibility = View.GONE
                itemView.lbl_disabled.visibility = View.VISIBLE
            }
            when(userListItem.role) {
                "admin" -> {
                    itemView.lbl_disabled.visibility = View.GONE
                    itemView.lbl_manager.visibility = View.GONE
                    itemView.lbl_admin.visibility = View.VISIBLE
                }
                "manager" -> {
                    itemView.lbl_disabled.visibility = View.GONE
                    itemView.lbl_admin.visibility = View.GONE
                    itemView.lbl_manager.visibility = View.VISIBLE
                }
                else -> {
                    itemView.lbl_disabled.visibility = View.GONE
                    itemView.lbl_manager.visibility = View.GONE
                    itemView.lbl_admin.visibility = View.GONE
                }
            }

            Glide.with(context)
                .load(userListItem.photoUrl)
                .centerCrop()
                .placeholder(context.getDrawable(R.drawable.ic_person_white_24dp))
                .into(itemView.img_avatar)

            itemView.setOnClickListener { clickListener(userListItem) }
        }
    }
}