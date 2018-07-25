package com.localytics.android.sample.richpush

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import com.localytics.android.Localytics
import com.localytics.android.MessagingListenerV2Adapter
import com.localytics.android.PushCampaign

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Localytics.setLoggingEnabled(true)
        Localytics.autoIntegrate(this)
        Localytics.setMessagingListener(object : MessagingListenerV2Adapter() {
            override fun localyticsShouldShowPushNotification(campaign: PushCampaign): Boolean {
                val attributes = campaign.getAttributes()
                if (attributes != null) {
                    if (!TextUtils.isEmpty(attributes.get("image_url")) || !TextUtils.isEmpty(attributes.get("action_category"))) {
                        val intent = Intent(applicationContext, RichPushService::class.java)
                        intent.putExtras(from(attributes))
                        startService(intent)

                        // Prevent Localytics SDK from displaying the push. We'll do it manually
                        return false
                    }
                }

                return true
            }
        })
    }

    private fun from(map: Map<String, String>): Bundle {
        val bundle = Bundle(map.size)
        for ((key, value) in map) {
            bundle.putString(key, value)
        }

        return bundle
    }
}
