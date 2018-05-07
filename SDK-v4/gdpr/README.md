# GDPR Compliant example

Sample project demonstrating the integration of Localytics for a customer supporting GDPR on SDK versions below SDK v5.1.
This app wraps a few Localytics calls in order to ensure that no data is sent to Localytics when a user is opted out of data collection. This code is meant as a sample in order to show off an example implementation, and is not intended as a complete solution.  To ensure you have properly completed a GDPR compliant integration, please consult your Localytics Mobile Engagement Consultant.

## Prerequisites

This app integrates against SDK v4.5.1.

## Setup

1. In `LocalyticsGDPRWrapper` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
2. Build and run.

## Summary
This app is gated by an authentication screen.  It is required that the end user log into the app in order to use the underlying features.  As a result, we don't deal with anonymous users.