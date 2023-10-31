package com.nelsito.travelplan.user.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.TripListItemBinding
import com.nelsito.travelplan.databinding.UserListItemBinding

class UserListAdapter(private val context: Context, private val clickListener: (UserListItem) -> Unit): ListAdapter<UserListItem, RecyclerView.ViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingList = UserListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(bindingList)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(getItem(position) as UserListItem, context, clickListener)
    }

    private class UserViewHolder(private val binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userListItem: UserListItem, context: Context,
                 clickListener: (UserListItem) -> Unit) {
            binding.txtUsername.text = userListItem.username
            binding.txtEmail.text = userListItem.email

            if (userListItem.disabled) {
                binding.lblManager.visibility = View.GONE
                binding.lblAdmin.visibility = View.GONE
                binding.lblDisabled.visibility = View.VISIBLE
            }
            when(userListItem.role) {
                "admin" -> {
                    binding.lblDisabled.visibility = View.GONE
                    binding.lblManager.visibility = View.GONE
                    binding.lblAdmin.visibility = View.VISIBLE
                }
                "manager" -> {
                    binding.lblDisabled.visibility = View.GONE
                    binding.lblAdmin.visibility = View.GONE
                    binding.lblManager.visibility = View.VISIBLE
                }
                else -> {
                    binding.lblDisabled.visibility = View.GONE
                    binding.lblManager.visibility = View.GONE
                    binding.lblAdmin.visibility = View.GONE
                }
            }

            Glide.with(context)
                .load(userListItem.photoUrl)
                .centerCrop()
                .placeholder(context.getDrawable(R.drawable.ic_person_white_24dp))
                .into(binding.imgAvatar)

            binding.root.setOnClickListener { clickListener(userListItem) }
        }
    }
}