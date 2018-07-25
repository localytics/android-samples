package com.localytics.android.sample.gcmlistenerservice

import android.app.IntentService
import android.content.Intent
import android.util.Log

import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.localytics.android.Localytics

import java.io.IOException

class RegistrationIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val instanceID = InstanceID.getInstance(this)
            val token = instanceID.getToken("YOUR_SENDER_ID", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
            Localytics.setPushRegistrationId(token)

            // TODO send token your server
        } catch (e: IOException) {
            Log.d(TAG, "Failed to complete token refresh", e)
        }

    }

    companion object {
        private val TAG = RegistrationIntentService::class.java.simpleName
    }
}
