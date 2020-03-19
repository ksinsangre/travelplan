package com.nelsito.travelplan.domain

import android.os.Parcelable
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@Parcelize
data class Trip(val placeId: String, val destination: String, val description: String, val dateFrom: Long, val dateTo: Long, val pointsOfInterest: MutableList<String> = mutableListOf()) : Parcelable {
    fun daysToGo(): Int {
        return Duration.between(Instant.now(), Instant.ofEpochMilli(dateFrom)).toDays().toInt()
    }
}

@Parcelize
data class TripToAdd(val place: Place, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable