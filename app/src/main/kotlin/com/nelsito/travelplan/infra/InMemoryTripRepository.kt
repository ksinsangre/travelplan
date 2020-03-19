package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import kotlinx.coroutines.delay

class InMemoryTripRepository(private val repository: TripRepository) : TripRepository {
    private val myTrips = hashMapOf<String, Trip>()
    override suspend fun add(trip: Trip) : Boolean {
        myTrips[trip.placeId] = trip
        return repository.add(trip)
    }

    override suspend fun getTrips(user: FirebaseUser): List<Trip> {
        if (myTrips.size == 0) {
            repository.getTrips(user).forEach {
                myTrips[it.placeId] = it
            }
        }

        return myTrips.values.toList()
    }

    override suspend fun update(trip: Trip): Boolean {
        myTrips[trip.placeId] = trip
        return repository.update(trip)
    }

    override suspend fun find(placeId: String) : Trip {
        return if (myTrips.containsKey(placeId)) {
            myTrips[placeId]!!
        } else {
            repository.find(placeId)
        }
    }

    override fun remove(trip: Trip) {
        myTrips.remove(trip.placeId)
    }
}