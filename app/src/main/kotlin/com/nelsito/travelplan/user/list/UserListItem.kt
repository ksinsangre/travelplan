package com.nelsito.travelplan.user.list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserListItem(
    val uid: String,
    val username: String,
    val email: String,
    val photoUrl: String,
    val role: String,
    val disabled: Boolean
) : Parcelable
