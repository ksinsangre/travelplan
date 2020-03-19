package com.nelsito.travelplan.infra

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreTripRepository : TripRepository {
    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun add(tripToAdd: Trip) : Boolean {
        return suspendCoroutine { cont ->
            val user = auth.currentUser as FirebaseUser
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(tripToAdd.placeId)
                .set(tripToAdd.toDto(), SetOptions.merge()).addOnSuccessListener {
                    cont.resumeWith(Result.success(true))
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun getTrips(user: FirebaseUser): List<Trip> {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val trips = querySnapshot.documents.map { document ->
                        // The method is now being called after
                        // loading the users list.
                        document.toTrip()
                    }
                    cont.resume(trips)
                }.addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    override fun update(trip: Trip) {
        TODO("Not yet implemented")
    }

    override fun find(tripId: String): Trip {
        TODO("Not yet implemented")
    }

    override fun remove(trip: Trip) {
        TODO("Not yet implemented")
    }
}

fun Trip.toDto() : TripDto{
    return TripDto(placeId, destination, description, Timestamp(dateFrom / 1000, 0), Timestamp(dateTo / 1000, 0), pointsOfInterest)
}

data class TripDto(val place_id: String, val destination: String, val description: String, val date_from: Timestamp, val date_to: Timestamp, val points_of_interest: MutableList<String>)



fun DocumentSnapshot.toTrip(): Trip {
    val date_from = this["date_from"] as Timestamp
    val date_to = this["date_to"] as Timestamp
    return Trip(this["place_id"].toString(),
        this["destination"].toString(),
        this["description"].toString(),
        date_from.seconds * 1000,
        date_to.seconds * 1000,
        (this["points_of_interest"] as List<String>).toMutableList()
    )
}