package com.nelsito.travelplan.trips.list

import com.nelsito.travelplan.domain.Trip
import org.threeten.bp.Instant

data class TripListItem(val trip: Trip, val destination: String, val date: String, val description: String, val daysToGo: Int)

fun Trip.toTripListItem() : TripListItem {
    return TripListItem(this, this.destination, this.formatDate(),this.description, this.daysToGo(Instant.now()))
}