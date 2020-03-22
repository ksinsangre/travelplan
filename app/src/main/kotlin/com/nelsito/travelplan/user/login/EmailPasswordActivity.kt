package com.nelsito.travelplan.user.login

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nelsito.travelplan.R
import com.nelsito.travelplan.infra.InfraProvider
import kotlinx.android.synthetic.main.activity_email_password.*
import kotlinx.android.synthetic.main.activity_email_password.progress
import kotlinx.android.synthetic.main.activity_trip_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EmailPasswordActivity : AppCompatActivity(), CoroutineScope {
    private var failedAttempts: Int = 0
    private lateinit var auth: FirebaseAuth

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)

        job = Job()
        auth = FirebaseAuth.getInstance()

        btn_sign_in.setOnClickListener {
            signIn(txt_email.text.toString(), txt_password.text.toString())
        }
        
        btn_sign_up.setOnClickListener {
            createAccount(txt_email.text.toString(), txt_password.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }
    private fun createAccount(email: String, password: String) {
        Log.d("LoginEmail", "createAccount:$email")
        if (!validateForm()) {
            return
        }

        progress.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginEmail", "createUserWithEmail:success")
                    val user = auth.currentUser
                    user!!.sendEmailVerification().addOnCompleteListener {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginEmail", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                progress.visibility = View.GONE
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d("LoginEmail", "signIn:$email")
        if (!validateForm()) {
            return
        }

        progress.visibility = View.VISIBLE

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                failedAttempts += 1

                if (failedAttempts == 3) {
                    launch {
                        InfraProvider.provideUserRepository().blockUser(email)
                        Snackbar.make(txt_email, "Too many attempts. User is disabled", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginEmail", "signInWithEmail:success")
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginEmail", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

                progress.visibility = View.GONE
            }
        // [END sign_in_with_email]
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = txt_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            txt_email.error = "Required."
            valid = false
        } else {
            txt_email.error = null
        }

        val password = txt_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            txt_password.error = "Required."
            valid = false
        } else {
            txt_password.error = null
        }

        return valid
    }

}
