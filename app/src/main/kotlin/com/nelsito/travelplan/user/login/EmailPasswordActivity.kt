package com.nelsito.travelplan.user.login

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.databinding.ActivityEmailPasswordBinding
import com.nelsito.travelplan.infra.InfraProvider
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


    private lateinit var binding: ActivityEmailPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        job = Job()
        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            signIn(binding.txtEmail.text.toString(), binding.txtPassword.text.toString())
        }

        binding.btnSignUp.setOnClickListener {
            createAccount(binding.txtEmail.text.toString(), binding.txtPassword.text.toString())
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

        binding.progress.visibility = View.VISIBLE

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

                binding.progress.visibility = View.GONE
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d("LoginEmail", "signIn:$email")
        if (!validateForm()) {
            return
        }

        binding.progress.visibility = View.VISIBLE

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                failedAttempts += 1

                if (failedAttempts == 3) {
                    launch {
                        InfraProvider.provideUserRepository().blockUser(email)
                        Snackbar.make(binding.txtEmail, "Too many attempts. User is disabled", Snackbar.LENGTH_LONG).show()
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

                binding.progress.visibility = View.GONE
            }
        // [END sign_in_with_email]
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtEmail.error = "Required."
            valid = false
        } else {
            binding.txtEmail.error = null
        }

        val password = binding.txtPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.txtPassword.error = "Required."
            valid = false
        } else {
            binding.txtPassword.error = null
        }

        return valid
    }

}
