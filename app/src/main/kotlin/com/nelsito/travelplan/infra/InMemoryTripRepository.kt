package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd
import kotlinx.coroutines.delay
import kotlin.coroutines.suspendCoroutine

class InMemoryTripRepository : TripRepository {
    private val myTrips = mutableListOf<Trip>()
    override suspend fun add(tripToAdd: TripToAdd) {
        delay(2000)
        return suspendCoroutine {
            myTrips.add(
                Trip(System.currentTimeMillis().toString(), tripToAdd.placeId, tripToAdd.description, tripToAdd.dateFrom, tripToAdd.dateTo)
            )
        }
    }
}