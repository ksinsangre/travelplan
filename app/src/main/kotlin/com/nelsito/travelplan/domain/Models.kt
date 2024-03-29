package com.nelsito.travelplan.domain

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import kotlin.math.sin

@Parcelize
data class Trip(val placeId: String, val destination: String, val description: String, val dateFrom: Long, val dateTo: Long, val pointsOfInterest: MutableList<String> = mutableListOf(), val latLng: LatLng = LatLng(0.0, 0.0)) : Parcelable {
    fun daysToGo(since: Instant): Int {
        return Duration.between(since, Instant.ofEpochMilli(dateFrom)).toDays().toInt()
    }
}

@Parcelize
data class Search(val title: String, val description: String, val dateFrom: Long, val dateTo: Long) : Parcelable