package com.nelsito.travelplan.user.invite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.activity_invite.*

class InviteActivity : AppCompatActivity(), InviteView {

    private lateinit var presenter: InvitePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

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
                    presenter.sendInvitation(txt_email.text.toString())
                    true
                }
                else -> false
            }
        }

        presenter = InvitePresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_invite, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
