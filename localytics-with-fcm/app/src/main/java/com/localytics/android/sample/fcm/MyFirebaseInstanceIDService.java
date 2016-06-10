package com.localytics.android.sample.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.localytics.android.Localytics;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Localytics.setPushRegistrationId(refreshedToken);
    }
}
