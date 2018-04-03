package com.localytics.gdpr;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPreferencesUtil {

    private static final String PREFS_NAME = "demo-prefs";
    private static final String LOGGED_IN_KEY = "logged-in";

    static boolean isUserLoggedIn(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(LOGGED_IN_KEY, false);
    }

    static void setUserLoggedIn(Context c, boolean loggedIn) {
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LOGGED_IN_KEY, loggedIn);
        editor.apply();
    }

}
