package com.nelsito.travelplan.domain

import org.assertj.core.api.Assertions
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class TripShould {
    @Test
    fun `return the days to go to a future trip`() {
        //given
        val dateFrom = LocalDate.now(ZoneOffset.UTC).plusMonths(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val dateTo = LocalDate.now(ZoneOffset.UTC).plusMonths(3).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val aFutureTrip = Trip("some id", "some place id", "some detination", "some description", dateFrom, dateTo)
        //when
        val actual = aFutureTrip.daysToGo()
        //then
        val expected = 60
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `return negative when is a past trip`() {
        //given
        val dateFrom = LocalDate.now(ZoneOffset.UTC).minusMonths(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val dateTo = LocalDate.now(ZoneOffset.UTC).minusMonths(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val aFutureTrip = Trip("some id", "some place id", "some detination", "some description", dateFrom, dateTo)
        //when
        val actual = aFutureTrip.daysToGo()
        //then
        val expected = -60
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}