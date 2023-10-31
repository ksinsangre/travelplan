package com.nelsito.travelplan.trips.list

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.datepicker.MaterialDatePicker
import com.nelsito.travelplan.R
import com.nelsito.travelplan.databinding.ActivityEditTripBinding
import com.nelsito.travelplan.databinding.ActivityFilterBinding
import com.nelsito.travelplan.domain.Search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class FilterActivity : AppCompatActivity(), CoroutineScope, FilterView {
    private var date_from = 0.0.toLong()
    private var date_to = 0.0.toLong()
    private lateinit var pickerFrom: MaterialDatePicker<Long>
    private lateinit var pickerTo: MaterialDatePicker<Long>
    private lateinit var presenter: FilterPresenter
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var binding: ActivityFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
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
                R.id.menu_serch -> {
                    val title = binding.txtDestinationTitle.text.toString()
                    val description = binding.txtDescription.text.toString()
                    presenter.search(title, description)
                    true
                }
                else -> false
            }
        }

        pickerFrom = buildDatePicker()
        pickerTo = buildDatePicker()
        binding.txtDateFrom.setOnClickListener {
            pickerFrom.addOnPositiveButtonClickListener { presenter.setDateFrom(it) }
            pickerFrom.show(supportFragmentManager, pickerFrom.toString())
        }
        binding.txtDateTo.setOnClickListener {
            pickerTo.addOnPositiveButtonClickListener { presenter.setDateTo(it) }
            pickerTo.show(supportFragmentManager, pickerTo.toString())
        }

        val lastSearch = intent.getParcelableExtra<Search>("LAST_SEARCH")
        presenter = FilterPresenter(lastSearch!!)
        presenter.attachView(this)
    }

    private fun buildDatePicker(): MaterialDatePicker<Long> {
        val builder = datePickerBuilder()
        val picker = builder.build()
        return picker
    }

    private fun datePickerBuilder(): MaterialDatePicker.Builder<Long> {
        return MaterialDatePicker.Builder.datePicker()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun applySearch(search: Search) {
        val retIntent = Intent()
        retIntent.putExtra("SEARCH", search)
        setResult(Activity.RESULT_OK, retIntent)
        finish()
    }

    override fun showDateFrom(dateFrom: String) {
        binding.txtDateFrom.text = dateFrom
    }

    override fun showDateTo(dateTo: String) {
        binding.txtDateTo.text = dateTo
    }

    override fun showTitle(title: String) {
        binding.txtDestinationTitle.setText(title, TextView.BufferType.EDITABLE)
    }

    override fun showDescription(description: String) {
        binding.txtDescription.setText(description, TextView.BufferType.EDITABLE)
    }
}
