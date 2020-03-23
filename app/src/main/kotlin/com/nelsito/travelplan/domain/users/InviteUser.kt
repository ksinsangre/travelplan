package com.nelsito.travelplan.domain.users

class InviteUser(private val inviteLinkService: InviteLinkService, private val emailSender: EmailSender) {
    fun sendEmail(email: String) {
        val link = inviteLinkService.generateLink()
        emailSender.sendEmail(email, link)
    }

}
