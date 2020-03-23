package com.nelsito.travelplan.domain

import com.nelsito.travelplan.domain.users.InviteLinkService
import com.nelsito.travelplan.domain.users.EmailSender
import com.nelsito.travelplan.domain.users.InviteUser
import org.junit.Test
import org.mockito.Mockito.*

class InviteUserShould {
    @Test
    fun `send and email with a link`() {
        //given
        val email = "email@myfriend.com"
        val link = "www.travelplan.com/invite"
        val inviteLinkService = mock(InviteLinkService::class.java)
        `when`(inviteLinkService.generateLink()).thenReturn(link)
        val inviteSender = mock(EmailSender::class.java)
        val invite = InviteUser(inviteLinkService, inviteSender)
        //when
        invite.sendEmail(email)
        //then
        verify(inviteSender).sendEmail(email, link)
    }
}