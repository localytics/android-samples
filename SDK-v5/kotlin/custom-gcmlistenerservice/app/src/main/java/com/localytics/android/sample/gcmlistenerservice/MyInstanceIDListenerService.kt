package com.localytics.android.sample.gcmlistenerservice

import android.content.Intent

import com.google.android.gms.iid.InstanceIDListenerService

class MyInstanceIDListenerService : InstanceIDListenerService() {
    override fun onTokenRefresh() {
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }
}
