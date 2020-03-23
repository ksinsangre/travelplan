package com.nelsito.travelplan.domain.users

interface EmailSender {
    suspend fun sendEmail(email: String, link: String): Boolean
}
