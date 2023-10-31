package com.nelsito.travelplan.user.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityLoginBinding
import com.nelsito.travelplan.databinding.ActivityWaitForVerificationBinding
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.infra.FirebaseUserRepository
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
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


    private lateinit var binding: ActivityWaitForVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitForVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        job = Job()
        presenter = WaitForVerificationPresenter(this, InfraProvider.provideUserRepository())

        binding.lnkSignIn.setOnClickListener {
            openLogin()
        }
        binding.lnkVerifyEmail.setOnClickListener {
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
        binding.txtUsername.text = currentUser.firebaseUser.displayName
        binding.txtEmail.text = currentUser.firebaseUser.email
    }

    override fun openTripList() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showEmailSent() {
        Snackbar.make(binding.lnkVerifyEmail, "Email sent...", Snackbar.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(binding.lnkVerifyEmail, message, Snackbar.LENGTH_SHORT).show()
    }
}
