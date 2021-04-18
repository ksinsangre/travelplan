package com.nelsito.travelplan.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException


class ProfileActivity : AppCompatActivity() {

    companion object {
        const val PICK_IMAGE = 324
    }

    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        storage = Firebase.storage

        val user = FirebaseAuth.getInstance().currentUser!!
        showUserData(user)


        img_avatar.setOnClickListener {
            val profileIntent = Intent()
            profileIntent.type = "image/*"
            profileIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE)
        }
    }

    private fun showUserData(user: FirebaseUser) {
        txt_username.text = user.displayName
        txt_email.text = user.email

        if (user.photoUrl != null) {
            Glide.with(this).load(user.photoUrl).centerCrop()
                .placeholder(getDrawable(R.drawable.ic_person_white_24dp)).into(img_avatar)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun     saveProfilePicture(imagePath: Uri) {
        progress.visibility = View.VISIBLE
        val imageReference =
            storage.reference.child(FirebaseAuth.getInstance().uid!!).child("images")
                .child("profile")
        imageReference.putFile(imagePath).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageReference.downloadUrl
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Profile", "Image upload successful")
                    val user = FirebaseAuth.getInstance().currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(it.result)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            progress.visibility = View.GONE
                            if (task.isSuccessful) {
                                Log.d("Profile", "User profile updated.")
                            }
                        }
                } else {
                    progress.visibility = View.GONE
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data?.data != null) {
            try {
                val imageUri = data?.data!!



                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                img_avatar.setImageBitmap(bitmap)
                saveProfilePicture(imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
