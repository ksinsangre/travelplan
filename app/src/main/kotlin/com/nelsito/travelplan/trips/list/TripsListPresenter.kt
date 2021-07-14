package com.nelsito.travelplan.trips.list

import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.domain.LoadTrips
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class TripsListPresenter(
    private val tripsView: TripsView,
    private val tripRepository: TripRepository,
    private val loadTrips: LoadTrips
) {
    private val nextMonthSearch = buildNextMonthAsDefaultSearch()
    var lastSearch = nextMonthSearch

    suspend fun loadTrips() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val trips = loadTrips(user)
                .map {
                    val date = it.formatDate()
                    TripListItem(it, it.destination, date, it.description, it.daysToGo(Instant.now()))
                }
            tripsView.showTrips(trips)
        }
    }

    fun onDelete(trip: Trip) {

    }

    suspend fun searchTrips(search: Search) {
        this.lastSearch = search
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val trips = tripRepository.searchTrips(user, search)
                .map {
                    val date = it.formatDate()
                    TripListItem(it, it.destination, date, it.description, it.daysToGo(Instant.now()))
                }
            tripsView.showTrips(trips)
        }
    }

    private fun buildNextMonthAsDefaultSearch(): Search {
        return Search("", "", Instant.now().toEpochMilli(),
            Instant.now().atZone(ZoneOffset.UTC).toLocalDate().plusMonths(1).atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000)
    }

    fun clearFilter() {
        lastSearch = nextMonthSearch
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