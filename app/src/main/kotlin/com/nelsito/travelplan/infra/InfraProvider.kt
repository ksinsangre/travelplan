package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.UserRepository

class InfraProvider {
    companion object Provider {
        private val userRepository = FirebaseUserRepository()
        private val tripRepository = FirestoreTripRepository()
        fun provideTripRepository() : TripRepository {
            return tripRepository
        }

        fun provideUserRepository(): UserRepository {
            return userRepository
        }
    }
}