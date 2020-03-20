package com.nelsito.travelplan.domain

class LocalDateService : DateService {
    override fun now(): Long {
        return System.currentTimeMillis()
    }
}
