package com.nelsito.travelplan.user.admin

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.formatDate
import com.nelsito.travelplan.user.list.UserListItem
import kotlinx.android.synthetic.main.activity_edit_trip.*
import kotlinx.android.synthetic.main.activity_edit_trip.toolbar
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditUserActivity : AppCompatActivity(), EditUserView, CoroutineScope {
    private lateinit var presenter: EditUserPresenter

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.menu_save -> {
                    launch {
                        presenter.edit(txt_username.text.toString(), txt_email.text.toString())
                    }
                    true
                }
                else -> false
            }
        }

        job = Job()

        val user: UserListItem? = intent.getParcelableExtra("USER")
        if (user != null) {
            showUserInfo(user)
            presenter = EditUserPresenter(user, InfraProvider.provideUserRepository())
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            presenter.attachView(this@EditUserActivity)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.role_admin ->
                    if (checked) {
                        presenter.setAdmin()
                    }
                R.id.role_manager ->
                    if (checked) {
                        presenter.setManager()
                    }
                R.id.role_regular ->
                    if (checked) {
                        presenter.setRegular()
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showUserInfo(user: UserListItem) {
        txt_username.setText(user.username, TextView.BufferType.EDITABLE)
        txt_email.setText(user.email, TextView.BufferType.EDITABLE)
        txt_password.visibility = View.GONE
        when(user.role) {
            "admin" -> role_admin.isChecked = true
            "manager" -> role_manager.isChecked = true
            else -> role_regular.isChecked = true
        }
    }

    override fun userSaved() {
        //this will notify the previous activity that something changed
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showAdminWidgets() {
        roles_group.visibility = View.VISIBLE
    }
}
