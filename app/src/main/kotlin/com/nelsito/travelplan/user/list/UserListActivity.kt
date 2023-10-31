package com.nelsito.travelplan.user.list

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityInviteBinding
import com.nelsito.travelplan.databinding.ActivityUserListBinding
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.TripsListActivity
import com.nelsito.travelplan.user.admin.AddUserActivity
import com.nelsito.travelplan.user.admin.AdminProfileActivity
import com.nelsito.travelplan.user.invite.InviteActivity
import com.nelsito.travelplan.user.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserListActivity : AppCompatActivity(), UserListView, CoroutineScope {

    private lateinit var listAdapter: UserListAdapter
    private lateinit var presenter: UserListPresenter
    companion object {
        const val EDIT_REQ_CODE = 4343
        const val NEW_REQ_CODE = 3434
    }
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    private lateinit var binding: ActivityUserListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        job = Job()

        setupBottomBar()
        binding.fab.setOnClickListener { view ->
            startActivityForResult(Intent(this, AddUserActivity::class.java),
                NEW_REQ_CODE
            )
        }
        listAdapter = UserListAdapter(this) {
            val intent = Intent(this, AdminProfileActivity::class.java)
            intent.putExtra("USER", it)
            startActivityForResult(intent, EDIT_REQ_CODE)
        }
        binding.userList.adapter = listAdapter

        presenter = UserListPresenter(this, InfraProvider.provideUserRepository())
    }

    private fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Log Out") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        openAnonymous()
                    }
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun setupBottomBar() {
        binding.bottomAppBar.replaceMenu(R.menu.menu_user_list)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    // Do something for menu item 1
                    logout()
                    true
                }
                R.id.menu_map_trips -> {
                    openTripList()
                    true
                }
                R.id.menu_invite -> {
                    openInvite()
                    true
                }
                else -> false
            }
        }
    }

    private fun openInvite() {
        val intent = Intent(this, InviteActivity::class.java)
        startActivity(intent)
    }

    private fun openTripList() {
        val intent = Intent(this, TripsListActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        launch {
            presenter.loadUsers()
        }
    }

    override fun showUsers(list: List<UserListItem>) {
        listAdapter.submitList(list)
        binding.progress.visibility = View.GONE
        binding.userList.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQ_CODE || requestCode == NEW_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                launch {
                    binding.progress.visibility = View.VISIBLE
                    presenter.loadUsers()
                }
            }
        }
    }

    private fun openAnonymous() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}