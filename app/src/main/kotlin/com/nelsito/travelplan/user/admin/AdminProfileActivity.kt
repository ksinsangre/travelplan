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
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityAddUserBinding
import com.nelsito.travelplan.databinding.ActivityAdminProfileBinding
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.user.list.UserListActivity
import com.nelsito.travelplan.user.list.UserListItem
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
    private lateinit var listAdapter: AdminTripsListAdapter
    private lateinit var binding: ActivityAdminProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
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
            presenter = AdminProfilePresenter(user, InfraProvider.provideUserRepository(), InfraProvider.provideTripRepository())
        }

        listAdapter =
            AdminTripsListAdapter(deleteClickListener = {
                binding.progress.visibility = View.VISIBLE
                launch {
                    presenter.deleteTrip(it)
                }
            })
        binding.tripList.adapter = listAdapter
        if (user!!.disabled) binding.btnDisable.text = "Enable User" else "Disable User"
        binding.btnDisable.setOnClickListener {
            launch {
                presenter.toggleDisable()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            presenter.attachView(this@AdminProfileActivity)
        }
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
                binding.progress.visibility = View.VISIBLE
                launch {
                    presenter.deleteUser()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showUserData(user: UserListItem) {
        binding.txtUsername.text = user.username
        binding.txtEmail.text = user.email

        if (user.photoUrl.isNotEmpty()) {
            Glide.with(this).load(user.photoUrl).centerCrop()
                .placeholder(getDrawable(R.drawable.ic_person_white_24dp)).into(binding.imgAvatar)
        }

        when(user.role) {
            "admin" -> binding.lblAdmin.visibility = View.VISIBLE
            "manger" -> binding.lblManager.visibility = View.VISIBLE
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

    override fun showTrips(trips: List<AdminTripListItem>) {
        binding.progress.visibility = View.GONE
        listAdapter.submitList(trips)
        if(trips.isNotEmpty()) {
            binding.tripList.visibility = View.VISIBLE
            binding.emptyPlaceholder.visibility = View.GONE
        } else {
            binding.tripList.visibility = View.GONE
            binding.emptyPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun userEnabled() {
        binding.btnDisable.text = "Disable User"
    }

    override fun userDisabled() {
        binding.btnDisable.text = "Enable User"
    }

    @Deprecated("Deprecated in Java")
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
