package com.nelsito.travelplan.domain.users

interface EmailSender {
    fun sendEmail(email: String, link: String)
}
