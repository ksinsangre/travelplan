package com.nelsito.travelplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_trips.*

class TripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        val user = FirebaseAuth.getInstance().currentUser
        txt_hello.text = "Hello ${user?.displayName}"
    }
}
