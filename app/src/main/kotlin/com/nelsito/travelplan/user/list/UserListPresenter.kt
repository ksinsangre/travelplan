package com.nelsito.travelplan.user.list

import com.nelsito.travelplan.domain.UserRepository

class UserListPresenter(private val userListView: UserListView, private val userRepository: UserRepository) {
    suspend fun loadUsers() {
        val list = userRepository.getUserList().map {
            UserListItem(it.uid, it.displayName, it.email, "", it.role)
        }
        userListView.showUsers(list)
    }
}

interface UserListView {
    fun showUsers(list: List<UserListItem>)
}
