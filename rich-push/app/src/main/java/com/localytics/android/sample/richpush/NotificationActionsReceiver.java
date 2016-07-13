package com.localytics.android.sample.richpush;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.localytics.android.Localytics;

public class NotificationActionsReceiver extends BroadcastReceiver
{
    private static final String TAG = NotificationActionsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Tag opened impression
        Localytics.handlePushNotificationOpened(intent);

        // Dismiss the notification - we could be smarter with request codes here...
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        String category = intent.getStringExtra("action_category");
        String action = intent.getStringExtra("action");

        Log.d(TAG, String.format("Notification action clicked. category=%s action=%s", category, action));

        // TODO launch an Activity, deep link, or just perform a background task
    }
}
