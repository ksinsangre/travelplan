package com.nelsito.travelplan

import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.*
import com.nelsito.travelplan.user.UserNavigationView

class UserNavigationPresenter(private val userView: UserNavigationView, private val userRepository: UserRepository) {
    suspend fun startNavigation() {
        when(userRepository.loadUser()) {
            is Admin -> userView.openUserList()
            is TravelManager -> userView.openUserList()
            is VerifiedUser -> userView.openTripList()
            is NotVerifiedUser -> userView.openWaitForVerificationScreen()
            is AnonymousUser -> userView.openLogin()
            else -> userView.openLogin()
        }
    }
}
