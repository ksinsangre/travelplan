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

    @Test
    fun `test formatter`() {
        //given
        val dateFrom = LocalDate.now(ZoneOffset.UTC).plusWeeks(1).plusMonths(2).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val format = "MMM dd, YYYY"

        val from = LocalDateTime.from(org.threeten.bp.Instant.ofEpochMilli(dateFrom).atZone(ZoneOffset.UTC)).format(
            DateTimeFormatter.ofPattern(format, Locale.getDefault()))
        //when
        //then
        val expected = "MAY 25, 2020"
        Assertions.assertThat(from).isEqualTo(expected)
    }
}