package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd

class FirestoreTripRepository : TripRepository {
    override suspend fun add(tripToAdd: TripToAdd) {
        TODO("Not yet implemented")
    }
}
