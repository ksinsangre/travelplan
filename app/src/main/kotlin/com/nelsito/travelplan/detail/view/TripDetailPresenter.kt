package com.nelsito.travelplan.detail.view

import com.google.android.libraries.places.api.model.Place

class TripDetailPresenter() {

    private lateinit var tripDetailView: TripDetailView
    private val listOfPOI = mutableListOf<PointOfInterestListItem>(PointOfInterestListItem.FooterPointOfInterestListItem("footer"))

    fun attachView(tripDetailView: TripDetailView) {
        this.tripDetailView = tripDetailView
        tripDetailView.showPointOfInterest(listOfPOI)
    }

    fun addPointOfInterest() {
        tripDetailView.showAddPointOfInterest()
    }

    fun pointOfInterestAdded(poiSelected: Place) {
        listOfPOI.add(listOfPOI.size - 1, PointOfInterestListItem.PlacePointOfInterestListItem(poiSelected))
        tripDetailView.showPointOfInterest(listOfPOI)
    }
}

interface TripDetailView {
    fun showAddPointOfInterest()
    fun showPointOfInterest(poiSelected: List<PointOfInterestListItem>)
}
