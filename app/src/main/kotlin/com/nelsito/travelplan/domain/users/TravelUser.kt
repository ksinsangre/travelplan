package com.nelsito.travelplan.domain.users

import com.google.firebase.auth.FirebaseUser

data class Admin(override val firebaseUser: FirebaseUser) : LoggedInUser(firebaseUser)

data class TravelManager(override val firebaseUser: FirebaseUser) : LoggedInUser(firebaseUser)

data class VerifiedUser(override val firebaseUser: FirebaseUser) : LoggedInUser(firebaseUser)

data class NotVerifiedUser(override val firebaseUser: FirebaseUser) : LoggedInUser(firebaseUser)

open class LoggedInUser(open val firebaseUser: FirebaseUser) : TravelUser

class AnonymousUser : TravelUser

interface TravelUser
