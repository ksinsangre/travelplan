package com.nelsito.travelplan.domain

import org.assertj.core.api.Assertions
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class TripShould {
    @Test
    fun `return the days to go to a future trip`() {
        //given
        val dateFrom = LocalDate.now(ZoneOffset.UTC).plusMonths(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val dateTo = LocalDate.now(ZoneOffset.UTC).plusMonths(3).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val aFutureTrip = Trip( "some place id", "some detination", "some description", dateFrom, dateTo)
        //when
        val actual = aFutureTrip.daysToGo()
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
        val actual = aFutureTrip.daysToGo()
        //then
        val expected = -86
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}