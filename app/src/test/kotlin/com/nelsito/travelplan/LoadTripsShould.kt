package com.nelsito.travelplan

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.DateService
import com.nelsito.travelplan.domain.LoadTrips
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter


@ExperimentalCoroutinesApi
class LoadTripsShould {
    @Test
    fun `return first closest future trips`() = runBlockingTest {
        //given
        val farTrip = aTrip(to= "Paris", on="01/10/2020", until="18/10/2020",
            withDescription="A trip to participate on my company's annual conference")
        val closeTrip = aTrip(to= "Buenos Aires", on="15/06/2020", until="30/07/2020",
            withDescription="Vacations to see the beauty of Patagonia")

        val repo = aRepo(listOf(farTrip, closeTrip))
        val loadTrips = LoadTrips(repo, aMockService("01/05/2020"))
        //when
        val actual = loadTrips(mock(FirebaseUser::class.java))
        //then
        val expected = listOf(closeTrip, farTrip)
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    private fun aMockService(now: String): DateService {
        val service = mock(DateService::class.java)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val nowMillis = LocalDate.parse(now, formatter).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        `when`(service.now()).thenReturn(nowMillis)
        return service
    }

    @Test
    fun `return past trips ordered by date descending`() = runBlockingTest {
        //given
        val closeTrip = aTrip(to= "Paris", on="01/10/2019", until="18/10/2019",
            withDescription="A trip to participate on my company's annual conference")
        val oldestTrip = aTrip(to= "Buenos Aires", on="15/06/2019", until="30/07/2019",
            withDescription="Vacations to see the beauty of Patagonia")

        val repo = aRepo(listOf(oldestTrip, closeTrip))
        val loadTrips = LoadTrips(repo, aMockService("01/05/2020"))
        //when
        val actual = loadTrips(mock(FirebaseUser::class.java))
        //then
        val expected = listOf(closeTrip, oldestTrip)
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `return future trips before past trips`() = runBlockingTest {
        //given
        val farFutureTrip = aTrip(to= "Paris", on="01/10/2020", until="18/10/2020",
            withDescription="A trip to participate on my company's annual conference")
        val closeFutureTrip = aTrip(to= "Buenos Aires", on="15/06/2020", until="30/07/2020",
            withDescription="Vacations to see the beauty of Patagonia")
        val closePastTrip = aTrip(to= "London", on="01/10/2019", until="18/10/2019",
            withDescription="A trip to participate on my company's annual conference")
        val oldestPastTrip = aTrip(to= "Cartagena", on="15/06/2019", until="30/07/2019",
            withDescription="Vacations to see the beauty of Patagonia")

        val repo = aRepo(listOf(farFutureTrip, oldestPastTrip, closeFutureTrip, closePastTrip))
        val loadTrips = LoadTrips(repo, aMockService("01/05/2020"))
        //when
        val actual = loadTrips(mock(FirebaseUser::class.java))
        //then
        val expected = listOf(closeFutureTrip, farFutureTrip, closePastTrip, oldestPastTrip)
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    private suspend fun aRepo(trips: List<Trip>): TripRepository {
        val user = mock(FirebaseUser::class.java)
        val repo = MockTripRepository()
        trips.forEach {
            repo.add(user, it)
        }
        return repo
    }

    private fun aTrip(to: String, on: String, until: String, withDescription: String): Trip {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFrom = LocalDate.parse(on, formatter).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val dateTo = LocalDate.parse(until, formatter).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        return Trip(System.currentTimeMillis().toString(), to, withDescription, dateFrom, dateTo)
    }
}