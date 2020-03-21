package com.nelsito.travelplan.user.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.infra.FirebaseUserRepository
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import kotlinx.android.synthetic.main.activity_wait_for_verification.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WaitForVerificationActivity : AppCompatActivity(), CoroutineScope, WaitForVerificationView {

    private lateinit var presenter: WaitForVerificationPresenter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_for_verification)
        job = Job()
        presenter = WaitForVerificationPresenter(this, InfraProvider.provideUserRepository())

        lnk_sign_in.setOnClickListener {
            openLogin()
        }
        lnk_verify_email.setOnClickListener {
            presenter.sendVerificationMail()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        launch {
            presenter.checkEmailVerification()
        }
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showUserData(currentUser: LoggedInUser) {
        txt_username.text = currentUser.firebaseUser.displayName
        txt_email.text = currentUser.firebaseUser.email
    }

    override fun openTripList() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showEmailSent() {
        Snackbar.make(lnk_verify_email, "Email sent...", Snackbar.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(lnk_verify_email, message, Snackbar.LENGTH_SHORT).show()
    }
}
