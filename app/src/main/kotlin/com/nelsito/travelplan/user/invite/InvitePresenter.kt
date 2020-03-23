package com.nelsito.travelplan.user.invite

import android.util.Log
import com.nelsito.travelplan.domain.users.InviteService
import com.nelsito.travelplan.domain.users.InviteUser

class InvitePresenter(private val inviteView: InviteView, private val inviteUser: InviteUser) {
    suspend fun sendInvitation(email: String) {
        if (inviteUser.sendEmail(email)) {
            try{
                inviteView.emailSent()
            } catch (e: Exception) {
                Log.e("Invite", e.message, e)
                inviteView.emailFailed(e.message ?: "")
            }
        }
    }
}

interface InviteView {
    fun emailSent()
    fun emailFailed(message: String)
}
