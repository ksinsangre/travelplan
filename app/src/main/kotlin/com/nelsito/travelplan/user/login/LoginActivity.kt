package com.nelsito.travelplan.user.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nelsito.travelplan.R
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.ui.UserNavigationPresenter
import com.nelsito.travelplan.user.UserNavigationView
import com.nelsito.travelplan.user.list.UserListActivity
import kotlinx.android.synthetic.main.activity_admin_profile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), UserNavigationView, CoroutineScope {

    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var presenter: UserNavigationPresenter
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        const val EMAIL_SIGN_IN = 4346
        const val GOOGLE_SIGN_IN = 4345
        const val FACEBOOK_SIGN_IN = 4347
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        job = Job()

        presenter = UserNavigationPresenter(this, InfraProvider.provideUserRepository())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        btn_email.setOnClickListener {
            val intent = Intent(this, EmailPasswordActivity::class.java)
            startActivityForResult(intent, EMAIL_SIGN_IN)
        }
        btn_google.setOnClickListener {
            googleSignIn()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EMAIL_SIGN_IN && resultCode == Activity.RESULT_OK) {
            launch {
                presenter.startNavigation()
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginGoogle", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginGoogle", "firebaseAuthWithGoogle:" + acct.id!!)
        progress.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    launch {
                        presenter.startNavigation()
                    }
                    Log.d("LoginGoogle", "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginGoogle", "signInWithCredential:failure", task.exception)
                    Snackbar.make(progress, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

                progress.visibility = View.GONE
            }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun openLogin() {
        //Nothing
    }

    override fun openUserList() {
        val intent = Intent(this, UserListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun openWaitForVerificationScreen() {
        val intent = Intent(this, WaitForVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun openTripList() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
