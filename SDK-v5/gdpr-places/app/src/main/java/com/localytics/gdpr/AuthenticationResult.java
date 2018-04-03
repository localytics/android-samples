package com.localytics.gdpr;

import android.support.annotation.Nullable;

public class AuthenticationResult {

    private final boolean success;
    @Nullable private final String customerId;
    private final boolean userOptedOut;

    public AuthenticationResult(final boolean success, @Nullable final String customerId, final boolean userOptedOut) {
        this.success = success;
        this.customerId = customerId;
        this.userOptedOut = userOptedOut;
    }

    boolean success() {
        return success;
    }

    boolean isUserOptedOut() {
        return userOptedOut;
    }

    @Nullable String getCustomerId() {
        return customerId;
    }
}
