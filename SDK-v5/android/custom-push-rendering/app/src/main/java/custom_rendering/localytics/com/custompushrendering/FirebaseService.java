package custom_rendering.localytics.com.custompushrendering;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.localytics.android.Localytics;
import com.localytics.android.PushTrackingActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        Localytics.tagPushReceivedEvent(data);

        try {
            //The Localytics Hash containing campaign information.
            if (data.get("ll") != null) {
                renderPushFromLocalytics(data);
            } else {
                //handle any other push providers if applicable
            }
        } catch (Exception e) {
            Log.e("Push Rendering", "Failed to extract Push Message", e);
        }

    }

    private void renderPushFromLocalytics(Map<String, String> data) throws JSONException {
        JSONObject ll = new JSONObject(data.get("ll"));
        String title = data.get("ll_title");
        String message = data.get("message");
        int campaignId = ll.getInt("ca");
        String channel = ll.getString("channel");

        if (!TextUtils.isEmpty(message)) {
            PendingIntent pendingIntent = generateContentIntent(data, campaignId);

            String pushChannelId = TextUtils.isEmpty(channel) ? "MY_PREFERRED_CHANNEL" : channel;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, pushChannelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(campaignId, notificationBuilder.build());
            }
        }
    }

    public PendingIntent generateContentIntent(Map<String, String> data, int campaignId) {
        //Instantiate this with Push Tracking Activity so that Localytics can properly tag push opened.
        Intent trackingIntent = new Intent(this, PushTrackingActivity.class);
        //Make sure to add all extras to the intent to ensure Localytics can tag Push Opened
        trackingIntent.putExtras(convertToBundle(data));
        tryAddDeeplinkIntent(trackingIntent, data);
        return PendingIntent.getActivity(this, campaignId, trackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void tryAddDeeplinkIntent(@NonNull Intent trackingIntent, @NonNull Map<String, String> data) {
        // Pull out the Localytics defined deeplink URL
        String deeplink = data.get("ll_deep_link_url");

        //If you have your own deeplinking logic, create the intent that you would like to deeplink here.
        Intent deeplinkIntent = null;
        // Setting the value of ll_launch_intent will direct Localytics to open this intent instead
        // of the activity defined with android.intent.category.LAUNCHER
        trackingIntent.putExtra("ll_launch_intent", deeplinkIntent);

        // If both a "ll_deep_link_url" is defined as well as a "ll_launch_intent", Localytics will
        // prioritize the "ll_deep_link_url" and use that as the destination.  If this is not
        // preferred behavior, then be sure to remove that key from the intent.

//        if (deeplink != null) {
//            trackingIntent.removeExtra("ll_deep_link_url");
//            data.remove("ll_deep_link_url");
//        }
    }

    @NonNull
    private Bundle convertToBundle(@NonNull Map<String, String> data) {
        Bundle bundle = new Bundle(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        return bundle;
    }
}
