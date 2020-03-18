package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd

class FirestoreTripRepository : TripRepository {
    override suspend fun add(tripToAdd: TripToAdd) {
        TODO("Not yet implemented")
    }

    override suspend fun getTrips(): List<Trip> {
        TODO("Not yet implemented")
    }
}
