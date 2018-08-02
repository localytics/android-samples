package com.localytics.android.sample.pushbroadcastreceiver;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.localytics.android.Localytics;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerWithGcm();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Localytics.onNewIntent(this, intent);
    }

    private void registerWithGcm()
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    String registrationId = gcm.register("YOUR_SENDER_ID");
                    Localytics.setPushRegistrationId(registrationId);

                    // TODO: Send registration ID to server
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Exception registering for GCM: " + e.getLocalizedMessage());

                    // TODO: Retry with exponential backoff
                }

                return null;
            }
        }.execute();
    }
}
