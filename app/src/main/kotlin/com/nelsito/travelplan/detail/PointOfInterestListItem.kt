package com.nelsito.travelplan.detail

import com.google.android.libraries.places.api.model.Place

sealed class PointOfInterestListItem {
    data class PlacePointOfInterestListItem(val place: Place) : PointOfInterestListItem()
    data class FooterPointOfInterestListItem(val text: String) : PointOfInterestListItem()
}