package com.nelsito.travelplan.user.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.LoggedInUser
import com.nelsito.travelplan.domain.users.TravelUser
import com.nelsito.travelplan.infra.InfraProvider
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class UserListActivity : AppCompatActivity(), UserListView, CoroutineScope {

    private lateinit var listAdapter: UserListAdapter
    private lateinit var presenter: UserListPresenter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_user_list)

        listAdapter = UserListAdapter(this)
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
    }
}