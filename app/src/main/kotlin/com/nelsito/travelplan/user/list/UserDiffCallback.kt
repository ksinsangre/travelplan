package com.nelsito.travelplan.user.list

import androidx.recyclerview.widget.DiffUtil

class UserDiffCallback : DiffUtil.ItemCallback<UserListItem>(){
    override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        return oldItem.email == newItem.email &&
                oldItem.username == newItem.username &&
                oldItem.role == newItem.role
    }
}
