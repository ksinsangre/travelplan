package com.nelsito.travelplan.user.list

import com.nelsito.travelplan.domain.UserRepository

class UserListPresenter(private val userListView: UserListView, private val userRepository: UserRepository) {
    suspend fun loadUsers() {
        val list = userRepository.getUserList().map {
            UserListItem(it.firebaseUser.uid, it.firebaseUser.displayName ?: "", it.firebaseUser.email ?: "", it.firebaseUser.photoUrl.toString())
        }
        userListView.showUsers(list)
    }
}

interface UserListView {
    fun showUsers(list: List<UserListItem>)
}
