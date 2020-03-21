package com.nelsito.travelplan.domain

import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.TravelUser
import com.nelsito.travelplan.infra.UserResponse

interface UserRepository {
    suspend fun loadUser(): TravelUser
    fun getCurrentUser(): LoggedInUser
    suspend fun getUserList(): List<UserResponse>
}
