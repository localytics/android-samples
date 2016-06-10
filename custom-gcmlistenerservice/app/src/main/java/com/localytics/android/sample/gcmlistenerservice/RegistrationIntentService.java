package com.localytics.android.sample.gcmlistenerservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.localytics.android.Localytics;

import java.io.IOException;

public class RegistrationIntentService extends IntentService
{
    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("YOUR_SENDER_ID", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Localytics.setPushRegistrationId(token);

            // TODO send token your server
        }
        catch (IOException e)
        {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}
