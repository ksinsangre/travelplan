package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository : UserRepository {
    override suspend fun loadUser(): TravelUser {
        return suspendCoroutine { cont ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                cont.resume(AnonymousUser())
            } else {
                user.reload().addOnCompleteListener {
                    user.getIdToken(false).addOnSuccessListener { result ->
                        if (result.claims.containsKey("role")) {
                            when (result.claims["role"].toString()) {
                                "admin" -> cont.resume(Admin(user))
                                "manager" -> cont.resume(TravelManager(user))
                                else -> isUser(cont, user)
                            }
                        } else {
                            isUser(cont, user)
                        }
                    }
                }
            }
        }
    }

    override fun getCurrentUser(): LoggedInUser {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null) {
            LoggedInUser(user)
        } else {
            throw AnonymousUserException()
        }
    }

    override suspend fun getUserList(): List<LoggedInUser> {
        TODO("Not yet implemented")
    }

    private fun isUser(cont: Continuation<TravelUser>, user: FirebaseUser) {
        if (user.isEmailVerified) {
            cont.resume(VerifiedUser(user))
        } else {
            cont.resume(NotVerifiedUser(user))
        }
    }
}

class AnonymousUserException : Throwable() {

}
