package com.nelsito.travelplanadmin.user.list

import androidx.recyclerview.widget.DiffUtil

class UserDiffCallback : DiffUtil.ItemCallback<UserListItem>(){
    override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem.email == newItem.email
    }
}
