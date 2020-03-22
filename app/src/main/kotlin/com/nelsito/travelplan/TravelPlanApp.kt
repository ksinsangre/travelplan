package com.nelsito.travelplan

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger


class TravelPlanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}