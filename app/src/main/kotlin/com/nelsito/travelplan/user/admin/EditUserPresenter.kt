package com.nelsito.travelplan.user.admin

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.Admin
import com.nelsito.travelplan.domain.users.TravelManager
import com.nelsito.travelplan.user.list.UserListItem

class EditUserPresenter(private var user: UserListItem, private val userRepository: UserRepository) {

    private lateinit var editUserView: EditUserView

    suspend fun attachView(editUserView: EditUserView) {
        this.editUserView = editUserView
        when(userRepository.loadUser()) {
            is Admin -> {
                editUserView.showAdminWidgets()
                loadTrips(user)
            }
        }
    }

    private fun loadTrips(user: UserListItem) {

    }

    suspend fun edit(username: String, email: String) {
        user = user.copy(username = username, email = email)
        userRepository.update(user)
        editUserView.userSaved()
    }

    fun setAdmin() {
        user = user.copy(role = "admin")
    }

    fun setManager() {
        user = user.copy(role = "manager")
    }

    fun setRegular() {
        user = user.copy(role = "regular")
    }
}

interface EditUserView {
    fun showUserInfo(user: UserListItem)
    fun userSaved()
    fun showAdminWidgets()
}
