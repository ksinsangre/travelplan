package com.nelsito.travelplan.user.admin

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityEditUserBinding
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.user.list.UserListItem
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

    private lateinit var binding: ActivityEditUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
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
                R.id.menu_save -> {
                    launch {
                        presenter.edit(binding.txtUsername.text.toString(), binding.txtEmail.text.toString())
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
        binding.txtUsername.setText(user.username, TextView.BufferType.EDITABLE)
        binding.txtEmail.setText(user.email, TextView.BufferType.EDITABLE)
        binding.txtPassword.visibility = View.GONE
        when(user.role) {
            "admin" -> binding.roleAdmin.isChecked = true
            "manager" -> binding.roleManager.isChecked = true
            else -> binding.roleRegular.isChecked = true
        }
    }

    override fun userSaved() {
        //this will notify the previous activity that something changed
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showAdminWidgets() {
        binding.rolesGroup.visibility = View.VISIBLE
    }
}
