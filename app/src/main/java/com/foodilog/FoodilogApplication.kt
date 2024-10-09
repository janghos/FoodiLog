package com.foodilog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodilogApplication : Application() {

    companion object {
        lateinit var prefs : SharedPrefHandler
    }

    override fun onCreate() {
        prefs = SharedPrefHandler(applicationContext)
        super.onCreate()
    }
}