# Push Configuration with a Custom GcmListenerService

Sample project demonstrating the integration of Localytics push messaging with a custom
GcmListenerService.

## Setup

1. Copy your `google-services.json` into the `app/`.
2. In `RegistrationIntentService` replace `YOUR_SENDER_ID` with your project's sender ID/number.
3. In `AndroidManifest.xml` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
4. Build and run.