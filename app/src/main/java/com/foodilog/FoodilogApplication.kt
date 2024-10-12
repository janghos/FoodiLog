package com.foodilog

import android.app.Application
import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodilogApplication : Application() {

    companion object {
        lateinit var prefs : SharedPrefHandler
        lateinit var placesClient: PlacesClient
        lateinit var context : Context
    }

    override fun onCreate() {
        prefs = SharedPrefHandler(applicationContext)
        Places.initialize(this, KeyConstant.API_KEY)
        // Create PlacesClient
        placesClient = Places.createClient(this)
        super.onCreate()
        context = this
    }
}