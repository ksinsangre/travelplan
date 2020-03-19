package com.nelsito.travelplan.trips.edit

import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository

class EditTripPresenter(private var trip: Trip, private val editTripView: EditTripView, val tripRepository: TripRepository) {
    fun dateChanged(dateFrom: Long, dateTo: Long) {
        trip = trip.copy(dateFrom = dateFrom, dateTo = dateTo)
        editTripView.showTripInfo(trip)
    }

    suspend fun save(description: String) {
        trip = trip.copy(description = description)
        tripRepository.update(trip)
        editTripView.tripSaved()
    }
}

interface EditTripView {
    fun showTripInfo(trip: Trip)
    fun tripSaved()
}
