package com.localytics.gdpr;

import android.app.Application;
import android.os.HandlerThread;

import com.localytics.android.Localytics;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final LocalyticsGDPRWrapper localyticsGDPRWrapper = LocalyticsGDPRWrapper.getInstance();
        localyticsGDPRWrapper.setLoggingEnabled(true);
        localyticsGDPRWrapper.pauseDataUploading(true);
        localyticsGDPRWrapper.integrate(this);

        //If the user is not logged in then they will be forced to login
        if (SharedPreferencesUtil.isUserLoggedIn(this)) {
            HandlerThread thread = new HandlerThread("opt-out-check", android.os.Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            new android.os.Handler(thread.getLooper()).post(new Runnable() {

                @Override
                public void run() {
                    String customerId = localyticsGDPRWrapper.getCustomerId();
                    // TODO A proper backend integration should be connected here that will test the user's
                    // opt out status
                    boolean isUserPrivacyOptedOut = false;

                    localyticsGDPRWrapper.setPrivacyOptedOut(isUserPrivacyOptedOut);
                    if (!isUserPrivacyOptedOut) {
                        // If you are using Places then you should uncomment the below line (and make sure to import play-services-location).
//                        localyticsGDPRWrapper.setLocationMonitoringEnabled(true);
                    }
                    localyticsGDPRWrapper.pauseDataUploading(false);
                }
            });
        } else {
            // If you are using Places then you should uncomment the below line (and make sure to import play-services-location).
            // localyticsGDPRWrapper.setLocationMonitoringEnabled(true);
        }
    }
}
