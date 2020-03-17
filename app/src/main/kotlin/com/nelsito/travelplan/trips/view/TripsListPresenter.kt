package com.nelsito.travelplan.trips.view

import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.trips.domain.Trip
import kotlinx.coroutines.delay

class TripsListPresenter(private val tripsView: TripsView) {
    suspend fun loadTrips() {
        val user = FirebaseAuth.getInstance().currentUser

        val trips = mockTrips()

        tripsView.showTrips(trips)
    }

    private suspend fun mockTrips(): List<TripListItem> {
        delay(5000)

        val mockTrip1 = TripListItem(Trip("1"), "Paris", "Oct 15th, 2020 / Nov 2nd, 2020",
            listOf("https://lh6.googleusercontent.com/proxy/fPDu46ymf2b3prhP_xHygrju1TsBcaH3o-ijbrTHQ3I2ZBGO-yyNyj3RCNSRaZIw-6DOBpmHR__0jkgQTvpF50wd7UgjCrdIy4qNPWA_2cbdiC9XDDuTZfQ936qRaEsb6LM5NQSJXxhf5r6LzzSPpqtffjkcxiKy2_sfHsvhsCl1f49kCtgQ5hFj4XJjI7Y9D84",
            "https://images.adsttc.com/media/images/5d44/14fa/284d/d1fd/3a00/003d/newsletter/eiffel-tower-in-paris-151-medium.jpg?1564742900"),
        "My company's annual conference", 45)
        val mockTrip2 = TripListItem(Trip("2"), "London", "Oct 15th, 2020 / Nov 2nd, 2020",
            listOf("https://lh6.googleusercontent.com/proxy/fPDu46ymf2b3prhP_xHygrju1TsBcaH3o-ijbrTHQ3I2ZBGO-yyNyj3RCNSRaZIw-6DOBpmHR__0jkgQTvpF50wd7UgjCrdIy4qNPWA_2cbdiC9XDDuTZfQ936qRaEsb6LM5NQSJXxhf5r6LzzSPpqtffjkcxiKy2_sfHsvhsCl1f49kCtgQ5hFj4XJjI7Y9D84",
                "https://images.adsttc.com/media/images/5d44/14fa/284d/d1fd/3a00/003d/newsletter/eiffel-tower-in-paris-151-medium.jpg?1564742900"),
            "My company's annual conference", 45)
        val mockTrip3 = TripListItem(Trip("3"), "Buenos Aires", "Oct 15th, 2020 / Nov 2nd, 2020",
            listOf("https://lh6.googleusercontent.com/proxy/fPDu46ymf2b3prhP_xHygrju1TsBcaH3o-ijbrTHQ3I2ZBGO-yyNyj3RCNSRaZIw-6DOBpmHR__0jkgQTvpF50wd7UgjCrdIy4qNPWA_2cbdiC9XDDuTZfQ936qRaEsb6LM5NQSJXxhf5r6LzzSPpqtffjkcxiKy2_sfHsvhsCl1f49kCtgQ5hFj4XJjI7Y9D84",
                "https://images.adsttc.com/media/images/5d44/14fa/284d/d1fd/3a00/003d/newsletter/eiffel-tower-in-paris-151-medium.jpg?1564742900"),
            "My company's annual conference", -30)
        return listOf(mockTrip1, mockTrip2, mockTrip3)
    }

    fun onDelete(trip: Trip) {

    }

}

interface TripsView {
    fun showTrips(trips: List<TripListItem>)
}
