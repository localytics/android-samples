package com.localytics.places_dwell_time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.localytics.android.Localytics
import com.localytics.android.PlacesCampaign


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val campaign = extras!!.getParcelable<PlacesCampaign>(MyApplication.PLACES_CAMPAIGN_EXTRA)
        if (campaign != null) {
            Localytics.triggerPlacesNotification(campaign)
        }
    }
}
