package com.localytics.android.sample.fcm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.localytics.android.Localytics

class MainActivity : AppCompatActivity() {

    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    protected fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Localytics.onNewIntent(this, intent)
    }

    fun tagEvent(view: View) {
        Localytics.tagEvent("test")
    }
}
