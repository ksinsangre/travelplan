package com.nelsito.travelplan.mytrips

import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class TripsListPresenter(
    private val tripsView: TripsView,
    private val tripRepository: TripRepository
) {
    suspend fun loadTrips() {
        val user = FirebaseAuth.getInstance().currentUser
        val trips = tripRepository.getTrips(user)
            .map {
                val date = it.formatDate()
                TripListItem(it, it.destination, date, it.description, it.daysToGo())
            }


        tripsView.showTrips(trips)
    }

    fun onDelete(trip: Trip) {

    }

}

interface TripsView {
    fun showTrips(trips: List<TripListItem>)
}

fun Trip.formatDate(): String {
    val format = "MMM dd, YYYY"
    val from = LocalDate.from(Instant.ofEpochMilli(dateFrom).atZone(ZoneOffset.UTC))
        .format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    val to = LocalDate.from(Instant.ofEpochMilli(dateTo).atZone(ZoneOffset.UTC))
        .format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    return "$from / $to"
}