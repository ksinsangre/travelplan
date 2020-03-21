package com.nelsito.travelplan.user.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity(), UserListView {

    private lateinit var listAdapter: UserListAdapter
    private lateinit var presenter: UserListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        listAdapter = UserListAdapter(this)
        user_list.adapter = listAdapter

        presenter = UserListPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadUsers()
    }

    override fun showUsers(list: List<UserListItem>) {
        listAdapter.submitList(list)
    }
}
