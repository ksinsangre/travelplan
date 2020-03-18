package com.nelsito.travelplan.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(val id: String, val placeId: String, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable

@Parcelize
data class TripToAdd(val placeId: String, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable