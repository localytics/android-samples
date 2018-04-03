package com.localytics.gdpr;

import android.os.AsyncTask;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, AuthenticationResult> {

    private final String mEmail;
    private final String mPassword;
    private final LoginTaskHandler mHandler;

    UserLoginTask(String email, String password, LoginTaskHandler handler) {
        mEmail = email;
        mPassword = password;
        mHandler = handler;
    }

    @Override
    protected AuthenticationResult doInBackground(Void... params) {
        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return new AuthenticationResult(false, null, false);
        }

        // A proper backend integration should be connected here that will log the user in
        // and check their customer ID for their data opt out status
        String customerIdFromServer = "customerIdFromServer";
        boolean isUserPrivacyOptedOut = false;

        return new AuthenticationResult(true, customerIdFromServer, isUserPrivacyOptedOut);
    }

    @Override
    protected void onPostExecute(final AuthenticationResult result) {
        mHandler.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        mHandler.onCancelled();
    }

    public interface LoginTaskHandler {

        void onPostExecute(AuthenticationResult result);

        void onCancelled();
    }
}
