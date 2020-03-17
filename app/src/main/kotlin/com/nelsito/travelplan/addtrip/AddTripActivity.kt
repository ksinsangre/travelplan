package com.nelsito.travelplan.addtrip

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.nelsito.travelplan.R
import kotlinx.android.synthetic.main.activity_add_trip.*
import kotlinx.android.synthetic.main.content_edit.*
import java.util.*


class AddTripActivity : AppCompatActivity() {
    private lateinit var picker: MaterialDatePicker<Long>
    private var dateFromSelected: Long = 0
    private var dateToSelected: Long = 0
    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)
        setSupportActionBar(toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        initializePlaces()

        picker = buildDatePicker()
        txt_date.setOnClickListener {
            setDelay(it) { pickDate() }
        }

        txt_destination_title.setOnClickListener {
            // Set the fields to specify which types of place data to return after the user has made a selection.
            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent = Autocomplete
                .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Log.i("Places", "Place: " + place.name + ", " + place.id)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                if (data != null) {
                    val status = Autocomplete.getStatusFromIntent(data);
                    Log.i("Places", status.statusMessage)
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Places", "User Canceled")
            }
        }
    }

    private fun setDelay(view: View, callback: () -> Unit) {
        Handler().postDelayed({
            callback()
            hideKeyboardFrom(this, view)
        }, 75)
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun pickDate() {
        picker.show(supportFragmentManager, picker.toString())
    }

    private fun buildDatePicker(): MaterialDatePicker<Long> {
        val builder = datePickerBuilder()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            txt_date.text = "On ${picker.headerText}"
            dateFromSelected = it
        }
        return picker
    }

    private fun datePickerBuilder(): MaterialDatePicker.Builder<Long> {
        return MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.thisMonthInUtcMilliseconds())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initializePlaces() {
        val apiKey = getString(R.string.places_api_key)
        Log.d("Places", "API KEY $apiKey")
        if (apiKey == "") {
            Toast.makeText(this, "API KEY NOT FOUND", Toast.LENGTH_LONG).show()
            return
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }
}
