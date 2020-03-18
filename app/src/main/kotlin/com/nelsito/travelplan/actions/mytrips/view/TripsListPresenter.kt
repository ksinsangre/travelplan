package com.nelsito.travelplan.actions.mytrips.view

import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.domain.Trip
import kotlinx.coroutines.delay

class TripsListPresenter(
    private val tripsView: TripsView,
    val placesClient: PlacesClient
) {
    suspend fun loadTrips() {
        val user = FirebaseAuth.getInstance().currentUser

        val trips = mockTrips()

        tripsView.showTrips(trips)
    }

    private suspend fun mockTrips(): List<TripListItem> {
        delay(5000)

        return emptyList()
    }

    fun onDelete(trip: Trip) {

    }

}

interface TripsView {
    fun showTrips(trips: List<TripListItem>)
}
