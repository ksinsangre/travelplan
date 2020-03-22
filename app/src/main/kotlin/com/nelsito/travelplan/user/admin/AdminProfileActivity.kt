package com.nelsito.travelplan.user.admin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.nelsito.travelplan.R
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.user.list.UserListActivity
import com.nelsito.travelplan.user.list.UserListItem
import kotlinx.android.synthetic.main.activity_admin_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.img_avatar
import kotlinx.android.synthetic.main.activity_profile.toolbar
import kotlinx.android.synthetic.main.activity_profile.txt_email
import kotlinx.android.synthetic.main.activity_profile.txt_username
import kotlinx.android.synthetic.main.activity_trip_detail.*
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class AdminProfileActivity : AppCompatActivity(), AdminProfileView, CoroutineScope {

    private lateinit var presenter: AdminProfilePresenter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.menu_edit -> {
                    presenter.editUser()
                    true
                }
                R.id.menu_delete -> {
                    deleteUser()
                    true
                }
                else -> false
            }
        }
        job = Job()

        val user = intent.getParcelableExtra<UserListItem>("USER")
        if (user == null) {
            Log.e("Admin", "user is null")
            finish()
        } else {
            showUserData(user)
        }

        presenter = AdminProfilePresenter(user, this, InfraProvider.provideUserRepository())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_black, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    private fun deleteUser() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Delete") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                launch {
                    presenter.deleteTrip()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showUserData(user: UserListItem) {
        txt_username.text = user.username
        txt_email.text = user.email

        if (user.photoUrl.isNotEmpty()) {
            Glide.with(this).load(user.photoUrl).centerCrop()
                .placeholder(getDrawable(R.drawable.ic_person_white_24dp)).into(img_avatar)
        }

        when(user.role) {
            "admin" -> lbl_admin.visibility = View.VISIBLE
            "manger" -> lbl_manager.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editUser(user: UserListItem) {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("USER", user)
        startActivityForResult(intent, UserListActivity.EDIT_REQ_CODE)
    }

    override fun userDeleted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UserListActivity.EDIT_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}