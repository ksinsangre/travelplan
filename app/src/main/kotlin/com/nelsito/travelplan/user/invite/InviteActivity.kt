package com.nelsito.travelplan.user.invite

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityInviteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class InviteActivity : AppCompatActivity(), InviteView, CoroutineScope {

    private lateinit var presenter: InvitePresenter
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var binding: ActivityInviteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        job = Job()

        setSupportActionBar(binding.toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.menu_send -> {
                    binding.progress.visibility = View.VISIBLE
                    launch {
                        presenter.sendInvitation(binding.txtEmail.text.toString())
                    }
                    true
                }
                else -> false
            }
        }

        //val inviteUser = InviteUser(com.nelsito.travelplan.infra.InviteDynamicLink(),SendGridEmailSender(getString(R.string.send_grid_api_key)))
        //presenter = InvitePresenter(this, inviteUser)
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_invite, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun emailSent() {
        Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun emailFailed(message: String) {
        binding.progress.visibility = View.GONE
        Snackbar.make(binding.toolbar, message, Snackbar.LENGTH_LONG).show()
    }
}
