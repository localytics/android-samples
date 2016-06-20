package com.localytics.android.sample.pushbroadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

public class MyBroadcastReceiver extends BroadcastReceiver
{
    /**
     * All pushes received from Localytics will contain an 'll' string extra which can be parsed into
     * a JSON object. This JSON object contains performance tracking information, such as a campaign
     * ID. Any push received containing this 'll' string extra, should be handled by the Localytics
     * GcmListenerService. Any other push can be handled as you see fit.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.hasExtra("ll"))
        {
            Intent service = new Intent(context, com.localytics.android.GcmListenerService.class);
            service.setAction("com.google.android.c2dm.intent.RECEIVE");
            service.putExtras(intent);
            context.startService(service);
        }
        else
        {
            String message = intent.getStringExtra("message");
            if (!TextUtils.isEmpty(message))
            {
                Intent mainIntent = new Intent(context, MainActivity.class);
                PendingIntent launchIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentIntent(launchIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
        }
    }
}
