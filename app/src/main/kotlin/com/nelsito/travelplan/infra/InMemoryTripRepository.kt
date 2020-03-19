package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd
import kotlinx.coroutines.delay

class InMemoryTripRepository : TripRepository {
    private val myTrips = mutableListOf<Trip>()
    override suspend fun add(tripToAdd: TripToAdd) {
        delay(2000)
        myTrips.add(
            Trip(System.currentTimeMillis().toString(), tripToAdd.place.id ?: "", tripToAdd.place.name ?: "", tripToAdd.description, tripToAdd.dateFrom, tripToAdd.dateTo)
        )
    }

    override suspend fun getTrips(user: FirebaseUser?): List<Trip> {
        delay(2000)
        return myTrips.toList()
    }
}