package com.nelsito.travelplan.domain.users

class InviteUser(private val inviteLinkService: InviteLinkService, private val emailSender: EmailSender) {
    suspend fun sendEmail(email: String): Boolean {
        val link = inviteLinkService.generateLink()
        return emailSender.sendEmail(email, link)
    }

}
