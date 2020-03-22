package com.nelsito.travelplan.domain

import com.google.android.gms.tasks.Task
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.TravelUser
import com.nelsito.travelplan.infra.UserResponse
import com.nelsito.travelplan.user.admin.AddUser
import com.nelsito.travelplan.user.list.UserListItem

interface UserRepository {
    suspend fun loadUser(): TravelUser
    fun getCurrentUser(): LoggedInUser
    suspend fun getUserList(): List<UserResponse>
    suspend fun update(user: UserListItem)
    suspend fun deleteUser(user: UserListItem)
    suspend fun add(user: AddUser)
    suspend fun blockUser(email: String): String
    suspend fun enableUser(uid: String): String
}
