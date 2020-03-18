package com.nelsito.travelplan.domain

import android.os.Parcelable
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(val id: String, val placeId: String, val destination: String, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable {
    fun daysToGo(): Int {
        TODO("Not yet implemented")
    }
}

@Parcelize
data class TripToAdd(val place: Place, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable