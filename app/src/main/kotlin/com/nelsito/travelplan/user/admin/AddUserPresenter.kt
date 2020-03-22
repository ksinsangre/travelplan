package com.nelsito.travelplan.user.admin

import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.Admin
import com.nelsito.travelplan.user.list.UserListItem

class AddUserPresenter(private val userRepository: UserRepository) {

    private lateinit var role: String
    private lateinit var addUserView: AddUserView

    suspend fun attachView(editUserView: AddUserView) {
        this.addUserView = editUserView
        when(userRepository.loadUser()) {
            is Admin -> {
                editUserView.showAdminWidgets()
            }
        }
    }

    suspend fun add(username: String, email: String, password: String) {
        val user = AddUser(username, email, password, role)
        userRepository.add(user)
        addUserView.userSaved()
    }

    fun setAdmin() {
        this.role = "admin"
    }

    fun setManager() {
        this.role = "manager"
    }

    fun setRegular() {
        this.role = "regular"
    }
}

data class AddUser(val username: String, val email: String, val pasword: String, val role: String)

interface AddUserView {
    fun userSaved()
    fun showAdminWidgets()
}
