package com.localytics.android.sample.richpush;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.localytics.android.Localytics;
import com.localytics.android.MessagingListenerAdapter;
import com.localytics.android.PushCampaign;

import java.util.Map;

public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Localytics.setLoggingEnabled(true);
        Localytics.autoIntegrate(this);
        Localytics.setMessagingListener(new MessagingListenerAdapter()
        {
            @Override
            public boolean localyticsShouldShowPushNotification(@NonNull PushCampaign campaign)
            {
                Map<String, String> attributes = campaign.getAttributes();
                if (attributes != null)
                {
                    if (!TextUtils.isEmpty(attributes.get("image_url")) || !TextUtils.isEmpty(attributes.get("action_category")))
                    {
                        Intent intent = new Intent(getApplicationContext(), RichPushService.class);
                        intent.putExtras(from(attributes));
                        startService(intent);

                        // Prevent Localytics SDK from displaying the push. We'll do it manually
                        return false;
                    }
                }

                return true;
            }
        });
    }

    private Bundle from(Map<String, String> map)
    {
        Bundle bundle = new Bundle(map.size());
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        return bundle;
    }
}
