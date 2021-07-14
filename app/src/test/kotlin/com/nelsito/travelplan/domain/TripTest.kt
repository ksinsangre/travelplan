package com.nelsito.travelplan.domain

import org.assertj.core.api.Assertions
import org.junit.Test
import org.threeten.bp.*

class TripShould {
    @Test
    fun `return the days to go to a future trip`() {
        //given
        val dateFrom = 1612148400000
        val dateTo = 1614567600000
        val aFutureTrip = Trip( "some place id", "some detination", "some description", dateFrom, dateTo)
        //when
        val from = Instant.ofEpochMilli(1612148400000)
        val actual = aFutureTrip.daysToGo(from.minus(Duration.ofDays(60)))
        //then
        val expected = 60
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `return negative when is a past trip`() {
        //given
        val dateFrom = 1612148400000
        val dateTo = 1614567600000
        val aFutureTrip = Trip( "some place id", "some detination", "some description", dateFrom, dateTo)
        //when
        val from = Instant.ofEpochMilli(1612148400000)
        val actual = aFutureTrip.daysToGo(from.plus(Duration.ofDays(86)))
        //then
        val expected = -86
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}