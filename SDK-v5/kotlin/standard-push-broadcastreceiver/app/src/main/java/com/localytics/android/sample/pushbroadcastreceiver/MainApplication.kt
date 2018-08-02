package com.localytics.android.sample.pushbroadcastreceiver

import android.app.Application

import com.localytics.android.Localytics

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Localytics.setLoggingEnabled(true)
        Localytics.autoIntegrate(this)
    }
}
