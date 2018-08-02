# Rich Push Examples

Sample project demonstrating the integration of Localytics push messaging with custom
handling of particular rich push messages (e.g. images and notification actions).

## Setup

1. Copy your `google-services.json` into the `app/`.
2. In `MainActivity` replace `YOUR_SENDER_ID` with your project's sender ID/number.
3. In `AndroidManifest.xml` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
4. Build and run.

## Images

Send a push including an `image_url` key with an HTTP image URL value.

## Actions

Send a push including an `action_category` key with the value `social` or `product`.