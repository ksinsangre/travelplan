package com.nelsito.travelplan.user.list

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.TravelUser
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.user.admin.AdminProfileActivity
import com.nelsito.travelplan.user.admin.EditUserActivity
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class UserListActivity : AppCompatActivity(), UserListView, CoroutineScope {

    private lateinit var listAdapter: UserListAdapter
    private lateinit var presenter: UserListPresenter
    companion object {
        const val EDIT_REQ_CODE = 4343
    }
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_user_list)

        listAdapter = UserListAdapter(this) {
            val intent = Intent(this, AdminProfileActivity::class.java)
            intent.putExtra("USER", it)
            startActivityForResult(intent, EDIT_REQ_CODE)
        }
        user_list.adapter = listAdapter

        presenter = UserListPresenter(this, InfraProvider.provideUserRepository())
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
        progress.visibility = View.GONE
        user_list.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                launch {
                    progress.visibility = View.VISIBLE
                    presenter.loadUsers()
                }
            }
        }
    }
}