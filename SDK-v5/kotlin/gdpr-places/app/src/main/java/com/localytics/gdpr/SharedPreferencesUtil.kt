package com.localytics.gdpr

import android.content.Context
import android.content.SharedPreferences

internal object SharedPreferencesUtil {

    private val PREFS_NAME = "demo-prefs"
    private val LOGGED_IN_KEY = "logged-in"

    fun isUserLoggedIn(c: Context): Boolean {
        val prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(LOGGED_IN_KEY, false)
    }

    fun setUserLoggedIn(c: Context, loggedIn: Boolean) {
        val prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(LOGGED_IN_KEY, loggedIn)
        editor.apply()
    }

}
