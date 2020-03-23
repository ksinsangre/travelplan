package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.users.InviteLinkService

class InviteDynamicLink : InviteLinkService {
    override fun generateLink(): String {
        return "https://travelplan.page.link/Go1D"
    }
}