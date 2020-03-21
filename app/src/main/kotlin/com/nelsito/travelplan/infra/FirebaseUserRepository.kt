package com.nelsito.travelplan.infra

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository : UserRepository {

    private var client: FirebaseAdminNetworkClient

    init {
        /*val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okhttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://us-central1-travel-plan-5e3ef.cloudfunctions.net/api/")
            //.client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client = retrofit.create(FirebaseAdminNetworkClient::class.java)
    }

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

    override suspend fun getUserList(): List<UserResponse> {
        val token = getToken()
        return client.getUserList("Bearer $token").users
    }

    private suspend fun getToken(): String {
        return suspendCoroutine { cont ->
            val user = FirebaseAuth.getInstance().currentUser!!
            user.getIdToken(false)
                .addOnSuccessListener { result ->
                    cont.resumeWith(Result.success(result.token?:""))
                }
        }
    }
    private fun isUser(cont: Continuation<TravelUser>, user: FirebaseUser) {
        if (user.isEmailVerified) {
            cont.resume(VerifiedUser(user))
        } else {
            cont.resume(NotVerifiedUser(user))
        }
    }
}



interface FirebaseAdminNetworkClient {
    @GET("users")
    suspend fun getUserList(@Header("Authorization") token: String): UserAdminResponse
}

class AnonymousUserException : Throwable()