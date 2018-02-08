# Places Dwell Time example

Sample project demonstrating the integration of Localytics places with additional validation for dwell time.

## Setup

1. Copy your `google-services.json` into the `app/`.
2. In `AndroidManifest.xml` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
3. Build and run.

## Dashboard

Create places campaigns that will by default wait for a dwell time of 10 minutes to trigger. If you would like to customize the dwell time, add a key value to the campaign with a key of "dwell-time" and value representing the wait time in seconds.
