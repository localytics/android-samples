package com.localytics.android.sample.richpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.localytics.android.PushTrackingActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class RichPushService extends Service
{
    private static final String TAG = RichPushService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handleIntent(intent);
        return Service.START_NOT_STICKY;
    }

    private void handleIntent(final Intent intent)
    {
        if (!TextUtils.isEmpty(intent.getStringExtra("image_url")))
        {
            Picasso.with(this)
                    .load(intent.getStringExtra("image_url"))
                    .into(new Target()
                    {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                        {
                            showNotification(intent, bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable)
                        {
                            Log.e(TAG, "Image download failed");
                            RichPushService.this.stopSelf();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable)
                        {
                        }
                    });
        }
        else
        {
            showNotification(intent, null);
        }
    }

    private void showNotification(@NonNull Intent intent, @Nullable  Bitmap bitmap)
    {
        Intent trackingIntent = new Intent(this, PushTrackingActivity.class);
        trackingIntent.putExtras(intent); // add all extras PushTrackingActivity can properly tag open event
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, trackingIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String message = intent.getStringExtra("message");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Image Push Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        if (bitmap != null)
        {
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .setSummaryText(message));
        }
        else
        {
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message));
        }

        String actionCategory = intent.getStringExtra("action_category");
        if (!TextUtils.isEmpty(actionCategory))
        {
            switch (actionCategory)
            {
                case "social":
                    Intent likeIntent = new Intent(this, NotificationActionsReceiver.class);
                    likeIntent.putExtra("action", "like");
                    likeIntent.putExtras(intent); // add all extras so we can track opened in receiver
                    builder.addAction(
                            android.R.drawable.ic_media_play, // just picking a random icon
                            "Like",
                            PendingIntent.getBroadcast(this, 1, likeIntent, 0));

                    Intent shareIntent = new Intent(this, NotificationActionsReceiver.class);
                    shareIntent.putExtra("action", "share");
                    shareIntent.putExtras(intent); // add all extras so we can track opened in receiver
                    builder.addAction(
                            android.R.drawable.ic_media_next, // just picking a random icon
                            "Share",
                            PendingIntent.getBroadcast(this, 2, shareIntent, 0));
                    break;
                case "product":
                    Intent buyIntent = new Intent(this, NotificationActionsReceiver.class);
                    buyIntent.putExtra("action", "buy");
                    buyIntent.putExtras(intent); // add all extras so we can track opened in receiver
                    builder.addAction(
                            android.R.drawable.ic_media_pause, // just picking a random icon
                            "Buy",
                            PendingIntent.getBroadcast(this, 3, buyIntent, 0));
                    break;
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());

        // Stop the service
        stopSelf();
    }
}
