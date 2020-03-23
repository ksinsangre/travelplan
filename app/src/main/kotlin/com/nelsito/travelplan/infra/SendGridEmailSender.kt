package com.nelsito.travelplan.infra

import com.nelsito.travelplan.domain.users.EmailSender
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SendGridEmailSender(private val apiKey: String) : EmailSender {
    private val client: SendGridNetworkClient

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sendgrid.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client = retrofit.create(SendGridNetworkClient::class.java)
    }

    override suspend fun sendEmail(email: String, link: String): Boolean {
        return suspendCoroutine { cont ->
            val content = SendGridContent("text/plain", link)
            val to = SendGridEmail(email, email)
            val personalizations =
                SendGridPersonalizations(subject = "Join TravelPlan", to = listOf(to))
            val from = SendGridEmail("invite@travelplan.com", "Travel Plan")
            val sendGrid = SendGridDto(listOf(content), listOf(personalizations), from, from)
            client.send(sendGrid, "Bearer $apiKey").enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    cont.resumeWithException(t)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    cont.resumeWith(Result.success(true))
                }
            })
        }
    }
}

interface SendGridNetworkClient {
    @POST("mail/send")
    @Headers("content-type: application/json")
    fun send(@Body email: SendGridDto, @Header("authorization") authorization: String): Call<Void>
}

data class SendGridDto(
    val content: List<SendGridContent>,
    val personalizations: List<SendGridPersonalizations>,
    val from: SendGridEmail,
    val reply_to: SendGridEmail
)

data class SendGridContent(val type: String, val value: String)

data class SendGridPersonalizations(val to: List<SendGridEmail>, val subject: String)

data class SendGridEmail(val email: String, val name: String)
