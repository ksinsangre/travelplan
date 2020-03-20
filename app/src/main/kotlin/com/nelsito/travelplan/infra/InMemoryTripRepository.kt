package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import kotlinx.coroutines.delay
import java.util.*

class InMemoryTripRepository(private val repository: TripRepository) : TripRepository {
    private val myTrips = hashMapOf<String, Trip>()
    override suspend fun add(user: FirebaseUser, trip: Trip): Boolean {
        myTrips[trip.placeId] = trip
        return repository.add(user, trip)
    }

    override suspend fun getTrips(user: FirebaseUser): List<Trip> {
        if (myTrips.size == 0) {
            repository.getTrips(user).forEach {
                myTrips[it.placeId] = it
            }
        }

        return myTrips.values.toList()
    }

    override suspend fun searchTrips(user: FirebaseUser, search: Search): List<Trip> {
        return repository.searchTrips(user, search)
            .filter { search.title.isEmpty() || it.destination.toLowerCase(Locale.getDefault()).contains(search.title.toLowerCase(Locale.getDefault()))}
            .filter { search.description.isEmpty() || it.description.toLowerCase(Locale.getDefault()).contains(search.description.toLowerCase(Locale.getDefault())) }
    }

    override suspend fun update(user: FirebaseUser, trip: Trip): Boolean {
        myTrips[trip.placeId] = trip
        return repository.update(user, trip)
    }

    override suspend fun find(user: FirebaseUser, placeId: String): Trip {
        return if (myTrips.containsKey(placeId)) {
            myTrips[placeId]!!
        } else {
            repository.find(user, placeId)
        }
    }

    override suspend fun remove(user: FirebaseUser, trip: Trip): Boolean {
        myTrips.remove(trip.placeId)
        return repository.remove(user, trip)
    }
}