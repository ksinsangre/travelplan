package com.nelsito.travelplan.user

import com.nelsito.travelplan.domain.users.LoggedInUser

interface UserNavigationView {
    fun openLogin()
    fun openUserList()
    fun openWaitForVerificationScreen()
    fun openTripList()
}
