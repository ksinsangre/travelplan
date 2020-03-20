package com.nelsito.travelplan.domain

import com.google.firebase.auth.FirebaseUser

interface TripRepository {
    suspend fun getTrips(user: FirebaseUser): List<Trip>
    suspend fun remove(user: FirebaseUser, trip: Trip): Boolean
    suspend fun find(user: FirebaseUser, placeId: String): Trip
    suspend fun add(user: FirebaseUser, trip: Trip): Boolean
    suspend fun update(user: FirebaseUser, trip: Trip): Boolean
    suspend fun searchTrips(user: FirebaseUser, search: Search): List<Trip>
}
