package com.nelsito.travelplan.trips.map

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.trips.list.formatDate
import com.nelsito.travelplan.ui.OnSnapPositionChangeListener

class MapTripsPresenter(private val mapTripsView: MapTripsView, private val tripRepository: TripRepository) :
    OnSnapPositionChangeListener {
    private var trips = emptyList<MapTripListItem>()
    private var canShowInMap = false

    suspend fun loadTrips() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            trips = tripRepository.getTrips(user.uid)
                .map {
                    val date = it.formatDate()
                    MapTripListItem(it.placeId, it.destination, date, it.latLng, it.daysToGo())
                }
            if (canShowInMap) {
                mapTripsView.mapTrips(trips)
            }
        }
    }

    fun mapReady() {
        canShowInMap = true
        mapTripsView.mapTrips(trips)
    }

    override fun onSnapPositionChange(position: Int) {
        if(trips.size > position) {
            mapTripsView.moveCamera(trips[position].latLng)
        }
    }

}

interface MapTripsView {
    fun mapTrips(trips: List<MapTripListItem>)
    fun moveCamera(latLng: LatLng)
}
