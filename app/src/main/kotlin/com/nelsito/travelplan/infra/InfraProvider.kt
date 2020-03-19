package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.TripRepository

class InfraProvider {
    companion object Provider {
        private val tripRepository = InMemoryTripRepository(FirestoreTripRepository())
        fun provideTripRepository() : TripRepository {
            return tripRepository
        }
    }
}