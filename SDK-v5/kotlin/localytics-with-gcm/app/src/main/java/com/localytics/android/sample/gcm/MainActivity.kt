package com.localytics.android.sample.gcm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.localytics.android.Localytics

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Localytics.registerPush()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Localytics.onNewIntent(this, intent)
    }
}
