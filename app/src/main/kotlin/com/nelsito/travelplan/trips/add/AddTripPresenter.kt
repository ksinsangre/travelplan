package com.nelsito.travelplan.trips.add

import androidx.core.util.Pair
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository

class AddTripPresenter(private val addTripView: AddTripView, private val user: FirebaseUser, private val tripRepository: TripRepository) {
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
        val trip = Trip(destination.id!!, destination.name!!, description, dateFrom, dateTo)
        tripRepository.add(user, trip)
        addTripView.dismiss()
    }
}

interface AddTripView {
    fun dismiss()
}
