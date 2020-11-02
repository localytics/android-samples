package com.mriegelhaupt.onboarding

import android.app.Application
import com.localytics.androidx.AnalyticsListener
import com.localytics.androidx.AnalyticsListenerAdapter
import com.localytics.androidx.Localytics
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Localytics.autoIntegrate(this)
        Localytics.setAnalyticsListener(object : AnalyticsListenerAdapter {
            override fun localyticsSessionDidOpen(isFirst: Boolean, isUpgrade: Boolean, isResume: Boolean) {
                if (isFirst && !isResume) {
                    Localytics.forceInAppMessage(File("/android_asset/Onboarding-sample/index.html"))
                }
            }
        })
    }
}