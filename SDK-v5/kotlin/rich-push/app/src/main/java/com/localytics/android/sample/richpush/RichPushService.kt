package com.localytics.android.sample.richpush

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log

import com.localytics.android.PushTrackingActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class RichPushService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return Service.START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra("ll_attachment_url"))) {
            Picasso.with(this)
                    .load(intent.getStringExtra("ll_attachment_url"))
                    .into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                            showNotification(intent, bitmap)
                        }

                        override fun onBitmapFailed(errorDrawable: Drawable) {
                            Log.e(TAG, "Image download failed")
                            this@RichPushService.stopSelf()
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                    })
        } else {
            showNotification(intent, null)
        }
    }

    private fun showNotification(intent: Intent, bitmap: Bitmap?) {
        val trackingIntent = Intent(this, PushTrackingActivity::class.java)
        trackingIntent.putExtras(intent) // add all extras PushTrackingActivity can properly tag open event
        val pendingIntent = PendingIntent.getActivity(this, 0, trackingIntent,
                PendingIntent.FLAG_ONE_SHOT)

        val message = intent.getStringExtra("message")
        if (!TextUtils.isEmpty(message) || bitmap != null) {
            val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Image Push Message")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)

            if (bitmap != null) {
                builder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setSummaryText(message))
            } else {
                builder.setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
            }

            val actionCategory = intent.getStringExtra("action_category")
            if (!TextUtils.isEmpty(actionCategory)) {
                when (actionCategory) {
                    "social" -> {
                        val likeIntent = Intent(this, NotificationActionsReceiver::class.java)
                        likeIntent.putExtra("action", "like")
                        likeIntent.putExtras(intent) // add all extras so we can track opened in receiver
                        builder.addAction(
                                android.R.drawable.ic_media_play, // just picking a random icon
                                "Like",
                                PendingIntent.getBroadcast(this, 1, likeIntent, 0))

                        val shareIntent = Intent(this, NotificationActionsReceiver::class.java)
                        shareIntent.putExtra("action", "share")
                        shareIntent.putExtras(intent) // add all extras so we can track opened in receiver
                        builder.addAction(
                                android.R.drawable.ic_media_next, // just picking a random icon
                                "Share",
                                PendingIntent.getBroadcast(this, 2, shareIntent, 0))
                    }
                    "product" -> {
                        val buyIntent = Intent(this, NotificationActionsReceiver::class.java)
                        buyIntent.putExtra("action", "buy")
                        buyIntent.putExtras(intent) // add all extras so we can track opened in receiver
                        builder.addAction(
                                android.R.drawable.ic_media_pause, // just picking a random icon
                                "Buy",
                                PendingIntent.getBroadcast(this, 3, buyIntent, 0))
                    }
                }
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, builder.build())
        }

        // Stop the service
        stopSelf()
    }

    companion object {
        private val TAG = RichPushService::class.java.simpleName
    }
}
