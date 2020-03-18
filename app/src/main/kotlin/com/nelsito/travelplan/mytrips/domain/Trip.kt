package com.nelsito.travelplan.mytrips.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(val id: String, val placeId: String) : Parcelable