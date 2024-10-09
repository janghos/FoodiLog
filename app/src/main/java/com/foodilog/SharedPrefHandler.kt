package com.foodilog

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHandler(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_data", 0)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun putString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return prefs.getFloat(key, defValue)
    }

    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

}