package com.nelsito.travelplan.mytrips.view

import com.nelsito.travelplan.mytrips.domain.Trip

data class TripListItem(val trip: Trip, val destination: String, val date: String, val images: List<String>, val description: String, val daysToGo: Int)
