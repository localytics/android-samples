package com.localytics.places_dwell_time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.localytics.android.Localytics;
import com.localytics.android.PlacesCampaign;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Bundle extras = intent.getExtras();
        PlacesCampaign campaign = extras.getParcelable(MyApplication.PLACES_CAMPAIGN_EXTRA);
        if (campaign != null) {
            Localytics.triggerPlacesNotification(campaign);
        }
    }
}
