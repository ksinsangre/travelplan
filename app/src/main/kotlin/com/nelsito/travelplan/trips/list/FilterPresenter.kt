package com.nelsito.travelplan.trips.list

import com.nelsito.travelplan.domain.Search
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class FilterPresenter(private val lastSerch: Search) {
    private var dateTo: Long = 0
    private var dateFrom: Long = 0
    private lateinit var filterView: FilterView

    fun search(title: String, description: String) {
        filterView.applySearch(Search(title, description, dateFrom, dateTo))
    }

    fun attachView(filterView: FilterView) {
        this.filterView = filterView
        this.setDateTo(lastSerch.dateTo)
        this.setDateFrom(lastSerch.dateFrom)
        filterView.showTitle(lastSerch.title)
        filterView.showDescription(lastSerch.description)
    }

    fun setDateTo(dateTo: Long) {
        this.dateTo = dateTo
        val format = "MMM dd, YYYY"
        val formatted = Instant.ofEpochMilli(dateTo).atZone(ZoneOffset.UTC).format(
            DateTimeFormatter.ofPattern(format, Locale.getDefault()))
        filterView.showDateTo(formatted)
    }

    fun setDateFrom(dateFrom: Long) {
        this.dateFrom = dateFrom
        val format = "MMM dd, YYYY"
        val formatted = Instant.ofEpochMilli(dateFrom).atZone(ZoneOffset.UTC).format(
            DateTimeFormatter.ofPattern(format, Locale.getDefault()))
        filterView.showDateFrom(formatted)
    }
}

interface FilterView {
    fun applySearch(search: Search)
    fun showDateFrom(dateFrom: String)
    fun showDateTo(dateTo: String)
    fun showTitle(title: String)
    fun showDescription(description: String)
}
