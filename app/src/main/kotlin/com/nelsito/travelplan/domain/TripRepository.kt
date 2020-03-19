package com.nelsito.travelplan.domain

import com.google.firebase.auth.FirebaseUser

interface TripRepository {
    suspend fun add(tripToAdd: TripToAdd)
    suspend fun getTrips(user: FirebaseUser?): List<Trip>
    fun update(trip: Trip)
    fun find(placeId: String): Trip
}
