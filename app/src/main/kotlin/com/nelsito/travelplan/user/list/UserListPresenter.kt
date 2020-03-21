package com.nelsito.travelplan.user.list

import com.google.firebase.auth.FirebaseAuth

class UserListPresenter(private val userListView: UserListView) {
    fun loadUsers() {
        /*val list = FirebaseAuth.getInstance().listUsers(null).values.map {
            UserListItem(it.uid, it.displayName, it.email, it.photoUrl)
        }
        userListView.showUsers(list)*/
    }

}

interface UserListView {
    fun showUsers(list: List<UserListItem>)
}
