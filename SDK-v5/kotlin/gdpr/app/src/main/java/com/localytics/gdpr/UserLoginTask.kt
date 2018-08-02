package com.localytics.gdpr

import android.os.AsyncTask

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String, private val mHandler: LoginTaskHandler) : AsyncTask<Void, Void, AuthenticationResult>() {

    @Override
    override fun doInBackground(vararg params: Void): AuthenticationResult {
        try {
            // Simulate network access.
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            return AuthenticationResult(false, null, false)
        }

        // TODO A proper backend integration should be connected here that will log the user in
        // and check their customer ID for their data opt out status
        val customerIdFromServer = "customerIdFromServer"
        val isUserPrivacyOptedOut = false

        return AuthenticationResult(true, customerIdFromServer, isUserPrivacyOptedOut)
    }

    @Override
    override fun onPostExecute(result: AuthenticationResult) {
        mHandler.onPostExecute(result)
    }

    @Override
    override fun onCancelled() {
        mHandler.onCancelled()
    }

    interface LoginTaskHandler {

        fun onPostExecute(result: AuthenticationResult)

        fun onCancelled()
    }
}
