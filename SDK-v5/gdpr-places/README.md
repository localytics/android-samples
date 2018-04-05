# GDPR Compliant example

Sample project demonstrating the integration of Localytics for a customer supporting GDPR. You can click [here](https://docs.localytics.com/dev/android.html#gdpr) to read the Localytics Documentation on GDPR.

This app ensures that the correct opted out status is always reported to Localytics, including when that status has changed in another service, such as a webpage. 

## Prerequisites

This app requires SDK v5.1 and up in order to properly support GDPR. 

## Setup

1. In `localytics.xml` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
2. Replace empty sections left in `UserLoginTask#doInBackground` and `MyApplication` with appropriate server calls to log a user in as well as check their privacy opted out status. 
3. Build and run.

## Summary
This app is gated by an authentication screen.  It is required that the end user log into the app in order to use the underlying features.  As a result, we don't deal with anonymous users, and can always garauntee a GDPR compliant integration for end users. This app additionally re-enables Places so that location based messaging can still target the end user. 