package com.localytics.android.sample.pushbroadcastreceiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.text.TextUtils

import com.localytics.android.Localytics

class MyBroadcastReceiver : BroadcastReceiver() {
    /**
     * All pushes received from Localytics will contain an 'll' string extra which can be parsed into
     * a JSON object. This JSON object contains performance tracking information, such as a campaign
     * ID. Any push received containing this 'll' string extra, should be handled by the Localytics
     * SDK. Any other push can be handled as you see fit.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (!Localytics.displayPushNotification(intent.extras)) {
            val message = intent.getStringExtra("message")
            if (!TextUtils.isEmpty(message)) {
                val mainIntent = Intent(context, MainActivity::class.java)
                val launchIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val builder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        .setContentIntent(launchIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, builder.build())
            }
        }
    }
}
