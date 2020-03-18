package com.nelsito.travelplan.actions.addtrip

import androidx.core.util.Pair
import com.google.android.libraries.places.api.model.Place
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.TripToAdd

class AddTripPresenter(private val addTripView: AddTripView, private val tripRepository: TripRepository) {
    private lateinit var destination: Place
    private var dateFrom: Long = 0
    private var dateTo: Long = 0


    fun destinationSelected(placeSelected: Place) {
        this.destination = placeSelected
    }

    fun dateSelected(dateRange: Pair<Long, Long>) {
        dateFrom = dateRange.first!!
        dateTo = dateRange.second!!
    }

    suspend fun save(description: String) {
        val tripToAdd = TripToAdd(destination.id ?: "", description, dateFrom, dateTo)
        tripRepository.add(tripToAdd)
        addTripView.dismiss()
    }
}

interface AddTripView {
    fun dismiss()
}
