package com.nelsito.travelplan.domain

import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.TravelUser

interface UserRepository {
    suspend fun loadUser(): TravelUser
    fun getCurrentUser(): LoggedInUser
    suspend fun getUserList(): List<LoggedInUser>
}
