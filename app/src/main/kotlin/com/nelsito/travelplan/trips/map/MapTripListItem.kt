package com.nelsito.travelplan.trips.map

import com.google.android.gms.maps.model.LatLng

data class MapTripListItem(val placeId: String, val destination: String, val date: String, val latLng: LatLng, val daysToGo: Int)