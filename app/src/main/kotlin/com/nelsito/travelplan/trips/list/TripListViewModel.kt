package com.nelsito.travelplan.trips.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.domain.LoadTrips
import com.nelsito.travelplan.domain.LocalDateService
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

class TripListViewModel : ViewModel() {
    fun onTripClicked(trip: TripListItem) {
        _selectedTrip.value = trip
    }

    private val _selectedTrip = MutableLiveData<TripListItem>()
    val selectedTrip: LiveData<TripListItem> get() = _selectedTrip

    private val _trips = MutableLiveData<List<TripListItem>>()
    val trips: LiveData<List<TripListItem>> get() = _trips

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> get() = _progress

    fun loadTrips() {
        val repo = InfraProvider.provideTripRepository()
        val loadTrips = LoadTrips(repo, LocalDateService())
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            _progress.value = true
            viewModelScope.launch {
                _trips.value = loadTrips(user)
                    .map { it.toTripListItem() }
                _progress.value = false
            }
        }
    }

    private fun buildNextMonthAsDefaultSearch(): Search {
        return Search("", "", Instant.now().toEpochMilli(),
            Instant.now().atZone(ZoneOffset.UTC).toLocalDate().plusMonths(1).atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000)
    }
    private val nextMonthSearch = buildNextMonthAsDefaultSearch()
    var lastSearch = nextMonthSearch

    fun clearFilter() {
        lastSearch = nextMonthSearch
    }

    fun searchTrips(search: Search) {
        this.lastSearch = search
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModelScope.launch {
                _trips.value = InfraProvider.provideTripRepository().searchTrips(user, search)
                    .map { it.toTripListItem() }
            }
        }
    }

    fun delete(trip: Trip) {

    }
}