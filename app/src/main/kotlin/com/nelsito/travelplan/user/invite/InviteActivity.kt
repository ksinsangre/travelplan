package com.nelsito.travelplan.user.invite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.users.InviteUser
import com.nelsito.travelplan.infra.SendGridEmailSender
import kotlinx.android.synthetic.main.activity_invite.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        job = Job()

        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.menu_send -> {
                    progress.visibility = View.VISIBLE
                    launch {
                        presenter.sendInvitation(txt_email.text.toString())
                    }
                    true
                }
                else -> false
            }
        }

        val inviteUser = InviteUser(com.nelsito.travelplan.infra.InviteDynamicLink(),
            SendGridEmailSender(getString(R.string.send_grid_api_key)))
        presenter = InvitePresenter(this, inviteUser)
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
        progress.visibility = View.GONE
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show()
    }
}
