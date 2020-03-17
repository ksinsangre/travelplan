package com.nelsito.travelplan.trips.view

import com.nelsito.travelplan.trips.domain.Trip

data class TripListItem(val trip: Trip, val destination: String, val date: String, val images: List<String>, val description: String, val daysToGo: Int)
