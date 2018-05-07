package com.localytics.gdpr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.localytics.android.Localytics;

import java.util.HashMap;

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

        findViewById(R.id.event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalyticsGDPRWrapper.getInstance().tagEvent("sample event", new HashMap<String, String>(), 10L);
            }
        });

        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalyticsGDPRWrapper.getInstance().setProfileAttribute("sample profile attribute", "sample value", Localytics.ProfileScope.APPLICATION);
            }
        });

        if (!SharedPreferencesUtil.isUserLoggedIn(this)) {
            navigateToLogin();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        LocalyticsGDPRWrapper.getInstance().onNewIntent(this, intent);
    }

    private void logOutClicked() {
        LocalyticsGDPRWrapper.getInstance().logout();
        SharedPreferencesUtil.setUserLoggedIn(this, false);
        navigateToLogin();
    }

    private void setOptOutStatus(final boolean privacyOptedOut) {
        LocalyticsGDPRWrapper.getInstance().setPrivacyOptedOut(privacyOptedOut);
        LocalyticsGDPRWrapper.getInstance().pauseDataUploading(false);
        // TODO Make a call to your servers to update the status of data collection opt out for this user.
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
