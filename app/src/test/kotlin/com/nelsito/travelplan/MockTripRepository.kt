package com.nelsito.travelplan

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import kotlinx.coroutines.delay
import java.util.*

class MockTripRepository : TripRepository {
    private val myTrips = mutableListOf<Trip>()
    override suspend fun add(user: FirebaseUser, trip: Trip): Boolean {
        myTrips.add(trip)
        return true
    }

    override suspend fun getTrips(uid: String): List<Trip> {
        return myTrips
    }

    override suspend fun searchTrips(user: FirebaseUser, search: Search): List<Trip> {
        return myTrips
            .filter { search.title.isEmpty() || it.destination.toLowerCase(Locale.getDefault()).contains(search.title.toLowerCase(Locale.getDefault()))}
            .filter { search.description.isEmpty() || it.description.toLowerCase(Locale.getDefault()).contains(search.description.toLowerCase(Locale.getDefault())) }
    }

    override suspend fun update(user: FirebaseUser, trip: Trip): Boolean {
        myTrips[myTrips.indexOf(trip)] = trip
        return true
    }

    override suspend fun remove(uid: String, placeId: String): Boolean {
        myTrips.removeIf { it.placeId == placeId }
        return true
    }

    override suspend fun find(user: FirebaseUser, placeId: String): Trip {
        return myTrips.find { it.placeId == placeId }!!
    }
}