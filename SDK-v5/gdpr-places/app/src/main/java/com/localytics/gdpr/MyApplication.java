package com.localytics.gdpr;

import android.app.Application;
import android.os.HandlerThread;

import com.localytics.android.Localytics;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Localytics.setLoggingEnabled(true);
        Localytics.pauseDataUploading(true);
        Localytics.autoIntegrate(this);
        Localytics.setLocationMonitoringEnabled(true);

        //If the user is not logged in then they will be forced to login
        if (SharedPreferencesUtil.isUserLoggedIn(this)) {
            HandlerThread thread = new HandlerThread("opt-out-check", android.os.Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            new android.os.Handler(thread.getLooper()).post(new Runnable() {

                @Override
                public void run() {
                    String customerId = Localytics.getCustomerId();
                    // TODO A proper backend integration should be connected here that will test the user's
                    // opt out status
                    boolean isUserPrivacyOptedOut = false;

                    Localytics.setPrivacyOptedOut(isUserPrivacyOptedOut);
                    Localytics.pauseDataUploading(false);
                    Localytics.setLocationMonitoringEnabled(true);
                }
            });
        }
    }
}
