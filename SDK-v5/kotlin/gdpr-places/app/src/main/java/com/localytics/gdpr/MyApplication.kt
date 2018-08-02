package com.localytics.gdpr

import android.app.Application
import android.os.HandlerThread

import com.localytics.android.Localytics

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Localytics.setLoggingEnabled(true)
        Localytics.pauseDataUploading(true)
        Localytics.autoIntegrate(this)
        Localytics.setLocationMonitoringEnabled(true)

        //If the user is not logged in then they will be forced to login
        if (SharedPreferencesUtil.isUserLoggedIn(this)) {
            val thread = HandlerThread("opt-out-check", android.os.Process.THREAD_PRIORITY_BACKGROUND)
            thread.start()
            android.os.Handler(thread.looper).post {
                val customerId = Localytics.getCustomerId()
                // TODO A proper backend integration should be connected here that will test the user's
                // opt out status
                val isUserPrivacyOptedOut = false

                Localytics.setPrivacyOptedOut(isUserPrivacyOptedOut)
                Localytics.pauseDataUploading(false)
                Localytics.setLocationMonitoringEnabled(true)
            }
        }
    }
}
