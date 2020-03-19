package com.nelsito.travelplan.mytrips

import com.nelsito.travelplan.domain.Trip

data class TripListItem(val trip: Trip, val destination: String, val date: String, val description: String, val daysToGo: Int)