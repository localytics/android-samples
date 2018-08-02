package com.localytics.android.sample.richpush

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.localytics.android.Localytics

class NotificationActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Tag opened impression
        Localytics.handlePushNotificationOpened(intent)

        // Dismiss the notification - we could be smarter with request codes here...
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)

        val category = intent.getStringExtra("action_category")
        val action = intent.getStringExtra("action")

        Log.d(TAG, String.format("Notification action clicked. category=%s action=%s", category, action))

        // TODO launch an Activity, deep link, or just perform a background task
    }

    companion object {
        private val TAG = NotificationActionsReceiver::class.java.simpleName
    }
}
