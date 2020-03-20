package com.nelsito.travelplan.trips.edit

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.domain.Trip
import com.nelsito.travelplan.infra.InfraProvider
import com.nelsito.travelplan.trips.list.formatDate
import kotlinx.android.synthetic.main.activity_edit_trip.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditTripActivity : AppCompatActivity(), EditTripView, CoroutineScope {
    private lateinit var presenter: EditTripPresenter
    private lateinit var picker: MaterialDatePicker<Pair<Long, Long>>

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trip)

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
                        presenter.save(txt_description.text.toString())
                    }
                    true
                }
                else -> false
            }
        }

        picker = buildDatePicker()
        txt_date.setOnClickListener {
            pickDate()
        }
        job = Job()

        val trip: Trip? = intent.getParcelableExtra("Trip")
        if (trip != null) {
            showTripInfo(trip)
            presenter = EditTripPresenter(trip, FirebaseAuth.getInstance().currentUser!!, this, InfraProvider.provideTripRepository())
        } else {
            finish()
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

    private fun pickDate() {
        picker.show(supportFragmentManager, picker.toString())
    }

    private fun buildDatePicker(): MaterialDatePicker<Pair<Long, Long>> {
        val builder = datePickerBuilder()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            if (it.first != null && it.second != null) {
                presenter.dateChanged(it.first!!, it.second!!)
            }
        }
        return picker
    }

    private fun datePickerBuilder(): MaterialDatePicker.Builder<Pair<Long, Long>> {
        return MaterialDatePicker.Builder.dateRangePicker()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showTripInfo(trip: Trip) {
        txt_date.text = trip.formatDate()
        txt_description.setText(trip.description, TextView.BufferType.EDITABLE)
    }

    override fun tripSaved() {
        //this will notify the previous activity that something changed
        setResult(Activity.RESULT_OK)
        finish()
    }
}
