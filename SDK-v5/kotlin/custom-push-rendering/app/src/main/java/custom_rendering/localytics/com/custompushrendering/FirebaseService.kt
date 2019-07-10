package custom_rendering.localytics.com.custompushrendering

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.localytics.android.Localytics
import com.localytics.android.PushTrackingActivity

import org.json.JSONException
import org.json.JSONObject

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage!!.data
        Localytics.tagPushReceivedEvent(data)

        try {
            //The Localytics Hash containing campaign information.
            if (data["ll"] != null) {
                renderPushFromLocalytics(data)
            } else {
                //handle any other push providers if applicable
            }
        } catch (e: Exception) {
            Log.e("Push Rendering", "Failed to extract Push Message", e)
        }

    }

    @Throws(JSONException::class)
    private fun renderPushFromLocalytics(data: Map<String, String>) {
        val ll = JSONObject(data["ll"])
        val title = data["ll_title"]
        val message = data["message"]
        val campaignId = ll.getInt("ca")
        val channel = ll.getString("channel")

        if (!TextUtils.isEmpty(message)) {
            val pendingIntent = generateContentIntent(data, campaignId)

            val pushChannelId = if (TextUtils.isEmpty(channel)) "MY_PREFERRED_CHANNEL" else channel
            val notificationBuilder = NotificationCompat.Builder(this, pushChannelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(campaignId, notificationBuilder.build())
        }
    }

    fun generateContentIntent(data: Map<String, String>, campaignId: Int): PendingIntent {
        //Instantiate this with Push Tracking Activity so that Localytics can properly tag push opened.
        val trackingIntent = Intent(this, PushTrackingActivity::class.java)
        //Make sure to add all extras to the intent to ensure Localytics can tag Push Opened
        trackingIntent.putExtras(convertToBundle(data))
        tryAddDeeplinkIntent(trackingIntent, data)
        return PendingIntent.getActivity(this, campaignId, trackingIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun tryAddDeeplinkIntent(trackingIntent: Intent, data: Map<String, String>) {
        // Pull out the Localytics defined deeplink URL
        val deeplink = data["ll_deep_link_url"]

        //If you have your own deeplinking logic, create the intent that you would like to deeplink here.
        val deeplinkIntent: Intent? = null
        // Setting the value of ll_launch_intent will direct Localytics to open this intent instead
        // of the activity defined with android.intent.category.LAUNCHER
        trackingIntent.putExtra("ll_launch_intent", deeplinkIntent)
        // For customers on SDK 5.6.1+ it is required to add this local authentication token to 
        // validate this intent as a valid source. If no mathcing token is found, the Localytics 
        // SDK ignores this intent
        trackingIntent.putExtra("ll_launch_intent_token", Localytics.getLocalAuthenticationToken())

        // If both a "ll_deep_link_url" is defined as well as a "ll_launch_intent", Localytics will
        // prioritize the "ll_deep_link_url" and use that as the destination.  If this is not
        // preferred behavior, then be sure to remove that key from the intent.

//        if (deeplink != null) {
//            trackingIntent.removeExtra("ll_deep_link_url");
//            data.remove("ll_deep_link_url");
//        }
    }

    private fun convertToBundle(data: Map<String, String>): Bundle {
        val bundle = Bundle(data.size)
        data.forEach { key, value ->  bundle.putString(key, value) }
        return bundle
    }
}
