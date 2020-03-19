package com.nelsito.travelplan.domain

import com.google.firebase.auth.FirebaseUser

interface TripRepository {
    suspend fun add(trip: Trip): Boolean
    suspend fun getTrips(user: FirebaseUser): List<Trip>
    suspend fun update(trip: Trip): Boolean
    suspend fun find(placeId: String): Trip
    fun remove(trip: Trip)
}
