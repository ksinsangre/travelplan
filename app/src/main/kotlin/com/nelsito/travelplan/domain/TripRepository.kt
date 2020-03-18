package com.nelsito.travelplan.domain

interface TripRepository {
    suspend fun add(tripToAdd: TripToAdd)
}
