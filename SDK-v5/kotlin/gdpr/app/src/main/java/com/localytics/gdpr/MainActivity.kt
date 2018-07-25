package com.localytics.gdpr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import com.localytics.android.Localytics

class MainActivity : Activity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.opt_in).setOnClickListener({ setOptOutStatus(false) })

        findViewById<View>(R.id.opt_out).setOnClickListener({ setOptOutStatus(true) })

        findViewById<View>(R.id.logout).setOnClickListener({ logOutClicked() })

        if (!SharedPreferencesUtil.isUserLoggedIn(this)) {
            navigateToLogin()
        }
    }

    @Override
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Localytics.onNewIntent(this, intent)
    }

    private fun logOutClicked() {
        Localytics.setCustomerIdWithPrivacyOptedOut(null, false)
        SharedPreferencesUtil.setUserLoggedIn(this, false)
        navigateToLogin()
    }

    private fun setOptOutStatus(privacyOptedOut: Boolean) {
        Localytics.setPrivacyOptedOut(privacyOptedOut)
        Localytics.pauseDataUploading(false)
        // TODO Make a call to your servers to update the status of data collection opt out for this user.
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
