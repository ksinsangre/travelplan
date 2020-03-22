package com.nelsito.travelplan.user.admin

import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.Admin
import com.nelsito.travelplan.trips.list.formatDate
import com.nelsito.travelplan.user.list.UserListItem

class AdminProfilePresenter(private var user: UserListItem,
                            private val userRepository: UserRepository,
                            private val tripRepository: TripRepository) {
    private lateinit var adminProfileView: AdminProfileView

    fun editUser() {
        adminProfileView.editUser(user)
    }

    suspend fun deleteUser() {
        userRepository.deleteUser(user)
        adminProfileView.userDeleted()
    }

    suspend fun attachView(adminProfileView: AdminProfileView) {
        this.adminProfileView = adminProfileView
        when(userRepository.loadUser()) {
            is Admin -> {
                loadTrips(user)
            }
        }
    }

    private suspend fun loadTrips(user: UserListItem) {
        val trips = tripRepository.getTrips(user.uid)
            .map {
                val date = it.formatDate()
                AdminTripListItem(it.placeId, it.destination, date)
            }
        adminProfileView.showTrips(trips)

    }

    suspend fun deleteTrip(trip: AdminTripListItem) {
        tripRepository.remove(user.uid, trip.placeId)
        loadTrips(user)
    }

    suspend fun toggleDisable() {
        if (user.disabled) {
            user = user.copy(disabled = false)
            userRepository.enableUser(user.uid)
            adminProfileView.userEnabled()
        } else {
            user = user.copy(disabled = true)
            userRepository.blockUser(user.email)
            adminProfileView.userDisabled()
        }
    }
}

interface AdminProfileView {
    fun editUser(user: UserListItem)
    fun userDeleted()
    fun showTrips(trips: List<AdminTripListItem>)
    fun userEnabled()
    fun userDisabled()
}
