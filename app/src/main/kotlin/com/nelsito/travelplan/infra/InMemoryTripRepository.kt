package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd
import kotlinx.coroutines.delay

class InMemoryTripRepository : TripRepository {
    private val myTrips = hashMapOf<String, Trip>()
    override suspend fun add(tripToAdd: TripToAdd) {
        delay(2000)
        myTrips[tripToAdd.place.id ?: ""] =
            Trip(System.currentTimeMillis().toString(), tripToAdd.place.id ?: "", tripToAdd.place.name ?: "", tripToAdd.description, tripToAdd.dateFrom, tripToAdd.dateTo)
    }

    override suspend fun getTrips(user: FirebaseUser?): List<Trip> {
        delay(2000)
        return myTrips.values.toList()
    }

    override fun update(trip: Trip) {
        myTrips[trip.placeId] = trip
    }

    override fun find(placeId: String) : Trip {
        return myTrips[placeId] ?: throw Exception("No trip found")
    }

    override fun remove(trip: Trip) {
        myTrips.remove(trip.placeId)
    }
}