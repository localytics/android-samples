package com.localytics.gdpr

import android.app.Application
import android.os.HandlerThread

import com.localytics.android.Localytics

class MyApplication : Application() {

    @Override
    override fun onCreate() {
        super.onCreate()

        Localytics.setLoggingEnabled(true)
        Localytics.pauseDataUploading(true)
        Localytics.autoIntegrate(this)

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
                if (!isUserPrivacyOptedOut) {
                    Localytics.setLocationMonitoringEnabled(true)
                }
                Localytics.pauseDataUploading(false)
            }
        } else {
            // If you are using Places then you should uncomment the below line (and make sure to import play-services-location).
            // Localytics.setLocationMonitoringEnabled(true);
        }
    }
}
