package com.nelsito.travelplan.user.login

import android.util.Log
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.VerifiedUser

class WaitForVerificationPresenter(private val view: WaitForVerificationView, private val userRepository: UserRepository) {

    init {
        view.showUserData(userRepository.getCurrentUser())
    }

    suspend fun checkEmailVerification() {
        when(userRepository.loadUser()) {
            is VerifiedUser -> view.openTripList()
            else -> view.showUserData(userRepository.getCurrentUser())
        }
    }

    fun sendVerificationMail() {
        userRepository.getCurrentUser().firebaseUser.sendEmailVerification().addOnSuccessListener {
            view.showEmailSent()
        }.addOnFailureListener {
            Log.e("Login", "sendEmailVerification exception", it)
            val message = "There was an error. Please try again."
            view.showErrorMessage(message)
        }
    }
}

interface WaitForVerificationView {
    fun showUserData(currentUser: LoggedInUser)
    fun openTripList()
    fun showEmailSent()
    fun showErrorMessage(message: String)
}