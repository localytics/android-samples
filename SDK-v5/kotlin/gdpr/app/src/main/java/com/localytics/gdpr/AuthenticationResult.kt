package com.localytics.gdpr

class AuthenticationResult(private val success: Boolean, internal val customerId: String?, internal val isUserOptedOut: Boolean) {

    internal fun success(): Boolean {
        return success
    }
}
