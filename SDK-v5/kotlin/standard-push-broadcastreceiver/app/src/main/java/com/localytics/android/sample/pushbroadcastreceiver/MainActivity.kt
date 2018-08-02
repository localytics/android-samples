package com.localytics.android.sample.pushbroadcastreceiver

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.gcm.GoogleCloudMessaging
import com.localytics.android.Localytics

import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerWithGcm()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Localytics.onNewIntent(this, intent)
    }

    private fun registerWithGcm() {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {
                    val gcm = GoogleCloudMessaging.getInstance(this@MainActivity)
                    val registrationId = gcm.register("YOUR_SENDER_ID")
                    Localytics.setPushRegistrationId(registrationId)

                    // TODO: Send registration ID to server
                } catch (e: IOException) {
                    Log.e(TAG, "Exception registering for GCM: " + e.localizedMessage)

                    // TODO: Retry with exponential backoff
                }

                return null
            }
        }.execute()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
