package com.localytics.places_dwell_time;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.localytics.android.CircularRegion;
import com.localytics.android.Localytics;
import com.localytics.android.LocationListener;
import com.localytics.android.MessagingListenerV2Adapter;
import com.localytics.android.PlacesCampaign;
import com.localytics.android.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MyApplication extends Application {

    public static final String PLACES_CAMPAIGN_EXTRA = "places-campaign";

    @Override
    public void onCreate() {
        super.onCreate();

        Localytics.setLoggingEnabled(true);
        Localytics.autoIntegrate(this);
        Localytics.setLocationMonitoringEnabled(true);
        LocalyticsListener listener = new LocalyticsListener();
        Localytics.setMessagingListener(listener);
        Localytics.setLocationListener(listener);
    }

    private class LocalyticsListener extends MessagingListenerV2Adapter implements LocationListener {

        private final AlarmManager alarmManager;
        private final Map<String, PendingIntent> activeDwellTimeCampaigns = new HashMap<>();

        LocalyticsListener() {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        @Override
        public boolean localyticsShouldShowPlacesPushNotification(@NonNull final PlacesCampaign campaign) {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            Bundle extras = new Bundle();
            extras.putParcelable(PLACES_CAMPAIGN_EXTRA, campaign);
            alarmIntent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            activeDwellTimeCampaigns.put(campaign.getRegion().getUniqueId(), pendingIntent);

            long dwellTimeFromCampaign = getDwellTimeFromCampaign(campaign);
            if (dwellTimeFromCampaign > 0) {
                long dwellTime = System.currentTimeMillis() + dwellTimeFromCampaign;
                alarmManager.set(AlarmManager.RTC_WAKEUP, dwellTime, pendingIntent);
                Toast.makeText(getApplicationContext(),
                               "Places campaign triggered, will wait for a dwell time (in seconds) of: "
                                       + TimeUnit.MILLISECONDS.toSeconds(dwellTimeFromCampaign), Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }
        @Override
        public void localyticsDidUpdateLocation(@Nullable final Location location) { }

        @Override
        public void localyticsDidTriggerRegions(@NonNull final List<Region> regions, @NonNull final Region.Event event) {
            if (event == Region.Event.EXIT && activeDwellTimeCampaigns.size() > 0) {
                for (Region region : regions) {
                    if (activeDwellTimeCampaigns.containsKey(region.getUniqueId())) {
                        PendingIntent intent = activeDwellTimeCampaigns .remove(region.getUniqueId());
                        alarmManager.cancel(intent);
                    }
                }
            }
        }

        @Override
        public void localyticsDidUpdateMonitoredGeofences(@NonNull final List<CircularRegion> list, @NonNull final List<CircularRegion> list1) { }

        private long getDwellTimeFromCampaign(final @NonNull PlacesCampaign campaign) {
            try {
                String dwellTimeString = campaign.getAttributes().get("dwell-time");
                if (dwellTimeString != null) {
                    long dwellTime = Long.parseLong(dwellTimeString);
                    if (dwellTime >= 0) {
                        return TimeUnit.SECONDS.toMillis(dwellTime);
                    }
                }
            } catch (Exception e) { /*Failed to parse attribute from campaign, falling back to default*/ }

            return TimeUnit.MINUTES.toMillis(10);
        }
    }
}
