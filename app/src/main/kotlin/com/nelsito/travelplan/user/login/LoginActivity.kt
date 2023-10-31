package com.nelsito.travelplan.user.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nelsito.travelplan.R
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.UserNavigationPresenter
import com.nelsito.travelplan.databinding.ActivityEmailPasswordBinding
import com.nelsito.travelplan.databinding.ActivityLoginBinding
import com.nelsito.travelplan.user.UserNavigationView
import com.nelsito.travelplan.user.list.UserListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), UserNavigationView, CoroutineScope {

    private var callbackManager: CallbackManager? = null
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

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        job = Job()

        presenter = UserNavigationPresenter(
            this,
            InfraProvider.provideUserRepository()
        )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        binding.btnEmail.setOnClickListener {
            val intent = Intent(this, EmailPasswordActivity::class.java)
            startActivityForResult(intent, EMAIL_SIGN_IN)
        }
        binding.btnGoogle.setOnClickListener {
            googleSignIn()
        }
        facebookSignIn()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager?.onActivityResult(requestCode, resultCode, data)

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
        binding.progress.visibility = View.VISIBLE

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
                    Snackbar.make(binding.progress, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

                binding.progress.visibility = View.GONE
            }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
        }
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

    fun facebookSignIn() {
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        binding.buttonFacebookLogin.setReadPermissions("email", "public_profile")
        binding.buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("LoginFacebook", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("LoginFacebook", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("LoginFacebook", "facebook:onError", error)
                // ...
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("LoginFacebook", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginFacebook", "signInWithCredential:success")
                    launch {
                        presenter.startNavigation()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginFacebook", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }
}
