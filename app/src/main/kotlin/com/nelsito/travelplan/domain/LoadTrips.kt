package com.nelsito.travelplan.domain

import com.google.firebase.auth.FirebaseUser

class LoadTrips(private val tripRepository: TripRepository, private val dateService: DateService) {
    suspend operator fun invoke(user: FirebaseUser): List<Trip> {
        val allTrips = tripRepository.getTrips(user)
        val sortedFutureTrips = allTrips
            .filter { it.dateFrom > dateService.now() }
            .sortedBy { it.dateFrom }
        val sortedPastTrips =  allTrips
            .filter { it.dateFrom <= dateService.now() }
            .sortedByDescending { it.dateFrom }

        val union = sortedFutureTrips.toMutableList()
        union.addAll(sortedPastTrips)
        return union
    }
}

interface DateService {
    fun now(): Long
}
