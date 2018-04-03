package com.localytics.gdpr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.localytics.android.Localytics;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.opt_in).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                setOptOutStatus(false);
            }
        });

        findViewById(R.id.opt_out).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                setOptOutStatus(true);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                logOutClicked();
            }
        });

        if (!SharedPreferencesUtil.isUserLoggedIn(this)) {
            navigateToLogin();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        Localytics.onNewIntent(this, intent);
    }

    private void logOutClicked() {
        Localytics.setCustomerIdWithPrivacyOptedOut(null, false);
        SharedPreferencesUtil.setUserLoggedIn(this, false);
        navigateToLogin();
    }

    private void setOptOutStatus(final boolean privacyOptedOut) {
        Localytics.setPrivacyOptedOut(privacyOptedOut);
        Localytics.pauseDataUploading(false);
        Localytics.setLocationMonitoringEnabled(true);
        //Make a call to your servers to update the status of data collection opt out for this user.
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
