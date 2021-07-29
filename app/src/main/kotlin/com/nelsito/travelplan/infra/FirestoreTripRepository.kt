package com.nelsito.travelplan.infra

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.nelsito.travelplan.domain.Search
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.domain.TripRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreTripRepository : TripRepository {
    private val db = FirebaseFirestore.getInstance()
    
    override suspend fun add(user: FirebaseUser, trip: Trip) : Boolean {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(trip.placeId)
                .set(trip.toDto(), SetOptions.merge()).addOnSuccessListener {
                    cont.resumeWith(Result.success(true))
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun getTrips(uid: String): List<Trip> {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(uid)
                .collection("trips")
                .orderBy("date_from", Query.Direction.DESCENDING)
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

    override suspend fun searchTrips(user: FirebaseUser, search: Search): List<Trip> {
        return suspendCoroutine { cont ->
            val trips = db.collection("users")
                .document(user.uid)
                .collection("trips")
                .whereGreaterThanOrEqualTo("date_from", Timestamp(search.dateFrom / 1000, 0))
                .whereLessThanOrEqualTo("date_from", Timestamp(search.dateTo / 1000, 0))
                .orderBy("date_from", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { querySnapshot ->
                    val result = querySnapshot.documents.map { document ->
                        // The method is now being called after
                        // loading the users list.
                        document.toTrip()
                    }
                    cont.resume(result)
                }.addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    override suspend fun update(user: FirebaseUser, trip: Trip) : Boolean {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(trip.placeId)
                .set(trip.toDto(), SetOptions.merge()).addOnSuccessListener {
                    cont.resumeWith(Result.success(true))
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun find(user: FirebaseUser, placeId: String): Trip {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(placeId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    cont.resume(documentSnapshot.toTrip())
                }.addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    override suspend fun remove(user: FirebaseUser, trip: Trip) : Boolean {
        return suspendCoroutine { cont ->
            db.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(trip.placeId)
                .delete()
                .addOnSuccessListener {
                    cont.resumeWith(Result.success(true))
                }.addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }
}

fun Trip.toDto() : TripDto{
    return TripDto(placeId, destination, description, Timestamp(dateFrom / 1000, 0), Timestamp(dateTo / 1000, 0), pointsOfInterest, GeoPoint(latLng.latitude, latLng.longitude))
}

data class TripDto(val place_id: String, val destination: String, val description: String, val date_from: Timestamp, val date_to: Timestamp, val points_of_interest: MutableList<String>, val location: GeoPoint)



fun DocumentSnapshot.toTrip(): Trip {
    val date_from = this["date_from"] as Timestamp
    val date_to = this["date_to"] as Timestamp
    return Trip(this["place_id"].toString(),
        this["destination"].toString(),
        this["description"].toString(),
        date_from.seconds * 1000,
        date_to.seconds * 1000,
        (this["points_of_interest"] as List<String>).toMutableList(),
        LatLng((this["location"] as GeoPoint).latitude, (this["location"] as GeoPoint).longitude)
    )
}