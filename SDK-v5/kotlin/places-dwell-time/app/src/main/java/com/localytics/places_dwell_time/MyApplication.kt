package com.localytics.places_dwell_time

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast

import com.localytics.android.CircularRegion
import com.localytics.android.Localytics
import com.localytics.android.LocationListener
import com.localytics.android.MessagingListenerV2Adapter
import com.localytics.android.PlacesCampaign
import com.localytics.android.Region

import java.util.HashMap
import java.util.concurrent.TimeUnit


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Localytics.setLoggingEnabled(true)
        Localytics.autoIntegrate(this)
        Localytics.setLocationMonitoringEnabled(true)
        val listener = LocalyticsListener()
        Localytics.setMessagingListener(listener)
        Localytics.setLocationListener(listener)
    }

    private inner class LocalyticsListener internal constructor() : MessagingListenerV2Adapter(), LocationListener {

        private val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        private val activeDwellTimeCampaigns = HashMap<String, PendingIntent>()

        override fun localyticsShouldShowPlacesPushNotification(campaign: PlacesCampaign): Boolean {
            val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
            val extras = Bundle()
            extras.putParcelable(PLACES_CAMPAIGN_EXTRA, campaign)
            alarmIntent.putExtras(extras)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT)
            activeDwellTimeCampaigns[campaign.region.uniqueId] = pendingIntent

            val dwellTimeFromCampaign = getDwellTimeFromCampaign(campaign)
            if (dwellTimeFromCampaign > 0) {
                val dwellTime = System.currentTimeMillis() + dwellTimeFromCampaign
                alarmManager.set(AlarmManager.RTC_WAKEUP, dwellTime, pendingIntent)
                Toast.makeText(applicationContext,
                        "Places campaign triggered, will wait for a dwell time (in seconds) of: " + TimeUnit.MILLISECONDS.toSeconds(dwellTimeFromCampaign), Toast.LENGTH_LONG).show()
                return false
            }

            return true
        }

        override fun localyticsDidUpdateLocation(location: Location?) {}

        override fun localyticsDidTriggerRegions(regions: List<Region>, event: Region.Event) {
            if (event == Region.Event.EXIT && activeDwellTimeCampaigns.size > 0) {
                for (region in regions) {
                    if (activeDwellTimeCampaigns.containsKey(region.uniqueId)) {
                        val intent = activeDwellTimeCampaigns.remove(region.uniqueId)
                        alarmManager.cancel(intent)
                    }
                }
            }
        }

        override fun localyticsDidUpdateMonitoredGeofences(list: List<CircularRegion>, list1: List<CircularRegion>) {}

        private fun getDwellTimeFromCampaign(campaign: PlacesCampaign): Long {
            try {
                val dwellTimeString = campaign.attributes["dwell-time"]
                if (dwellTimeString != null) {
                    val dwellTime = java.lang.Long.parseLong(dwellTimeString)
                    if (dwellTime >= 0) {
                        return TimeUnit.SECONDS.toMillis(dwellTime)
                    }
                }
            } catch (e: Exception) { /*Failed to parse attribute from campaign, falling back to default*/
            }

            return TimeUnit.MINUTES.toMillis(10)
        }
    }

    companion object {
        const val PLACES_CAMPAIGN_EXTRA = "places-campaign"
    }
}
