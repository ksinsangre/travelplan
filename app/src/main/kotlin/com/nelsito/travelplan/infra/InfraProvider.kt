package com.nelsito.travelplan.infra

import com.google.firebase.firestore.FirebaseFirestore
import com.nelsito.travelplan.domain.TripRepository

class InfraProvider {
    companion object Provider {
        private val tripRepository = InMemoryTripRepository(FirestoreTripRepository())
        fun provideTripRepository() : TripRepository {
            FirebaseFirestore.setLoggingEnabled(true)
            return tripRepository
        }
    }
}