package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd

class FirestoreTripRepository : TripRepository {
    override suspend fun add(tripToAdd: TripToAdd) {
        TODO("Not yet implemented")
    }

    override suspend fun getTrips(user: FirebaseUser?): List<Trip> {
        TODO("Not yet implemented")
    }

    override fun update(trip: Trip) {
        TODO("Not yet implemented")
    }

    override fun find(tripId: String): Trip {
        TODO("Not yet implemented")
    }

    override fun remove(trip: Trip) {
        TODO("Not yet implemented")
    }
}
