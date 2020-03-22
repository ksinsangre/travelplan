package com.nelsito.travelplan.user.admin

import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.user.list.UserListItem

class AdminProfilePresenter(private val user: UserListItem, private val adminProfileView: AdminProfileView, private val userRepository: UserRepository) {
    fun editUser() {
        adminProfileView.editUser(user)
    }

    suspend fun deleteTrip() {
        userRepository.deleteUser(user)
        adminProfileView.userDeleted()
    }
}

interface AdminProfileView {
    fun editUser(user: UserListItem)
    fun userDeleted()

}
