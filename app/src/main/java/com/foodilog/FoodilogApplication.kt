package com.foodilog

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodilogApplication : Application() {

    companion object {
        lateinit var prefs : SharedPrefHandler
        lateinit var placesClient: PlacesClient
    }

    override fun onCreate() {
        prefs = SharedPrefHandler(applicationContext)
        Places.initialize(this, KeyConstant.API_KEY)
        // Create PlacesClient
        placesClient = Places.createClient(this)
        super.onCreate()
    }
}