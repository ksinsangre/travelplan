package com.nelsito.travelplan.trips.add

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
import androidx.core.util.Pair
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityAddTripBinding
import com.nelsito.travelplan.infra.InfraProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class AddTripActivity : AppCompatActivity(), CoroutineScope,
    AddTripView {
    private var destinationSelected = false
    private var dateSelected = false
    private lateinit var presenter: AddTripPresenter
    private lateinit var picker: MaterialDatePicker<Pair<Long, Long>>

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var binding: ActivityAddTripBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        title = ""
        with(supportActionBar!!) {
            setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        initializePlaces()
        job = Job()

        picker = buildDatePicker()
        binding.txtDate.setOnClickListener {
            setDelay(it) { pickDate() }
        }

        binding.txtDestinationTitle.setOnClickListener {
            // Set the fields to specify which types of place data to return after the user has made a selection.
            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = Autocomplete
                .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .setTypeFilter(TypeFilter.REGIONS)
                .build(this)
            startActivityForResult(intent,
                AUTOCOMPLETE_REQUEST_CODE
            )
        }

        binding.btnSave.setOnClickListener {
            if (!destinationSelected) {
                binding.txtDestinationTitle.background = getDrawable(R.drawable.red_rounded_textview)
            }
            if (!dateSelected) {
                binding.txtDate.background = getDrawable(R.drawable.red_rounded_textview)
            }

            if (dateSelected && destinationSelected) {
                binding.progress.visibility = View.VISIBLE
                launch {
                    presenter.save(binding.inputDescription.text.toString())
                }
            }
        }

        presenter = AddTripPresenter(
            this,
            FirebaseAuth.getInstance().currentUser!!,
            InfraProvider.provideTripRepository()
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val placeSelected = Autocomplete.getPlaceFromIntent(data)
                    presenter.destinationSelected(placeSelected)
                    binding.txtDestinationTitle.text = placeSelected.name
                    binding.txtDestinationTitle.background = getDrawable(R.drawable.rounded_textview)
                    destinationSelected = true
                    Log.i("Places", "Place: " + placeSelected.name + ", " + placeSelected.id)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                if (data != null) {
                    val status = Autocomplete.getStatusFromIntent(data);
                    Log.i("Places", status.statusMessage ?: "No message")
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

    private fun buildDatePicker(): MaterialDatePicker<Pair<Long, Long>> {
        val builder = datePickerBuilder()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            binding.txtDate.text = picker.headerText
            dateSelected = true
            binding.txtDate.background = getDrawable(R.drawable.rounded_textview)
            presenter.dateSelected(it)
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

    private fun initializePlaces() {
        val apiKey = getString(R.string.api_key)
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

    override fun dismiss() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
