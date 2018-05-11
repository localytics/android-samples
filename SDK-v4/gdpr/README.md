# GDPR Compliant example

Sample project demonstrating the integration of Localytics for a customer supporting GDPR on SDK versions below SDK v5.1.
This app wraps a few Localytics calls in order to ensure that no data is sent to Localytics when a user is opted out of data collection. This code is meant as a sample in order to show off an example implementation, and is not intended as a complete solution. Specifically, this sample shows the integration of only a few Localytics calls: `tagEvent` and `setProfileAttribute`.  All additional Localytics API calls should be wrapped in a similar fashion. To ensure you have properly completed a GDPR compliant integration, please consult your Localytics Mobile Engagement Consultant before going live.

## Prerequisites

This app integrates against SDK v4.5.1.

## Setup

1. In `LocalyticsGDPRWrapper` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
2. Build and run.

## Summary
This app is gated by an authentication screen.  It is required that the end user log into the app in order to use the underlying features.  As a result, we don't deal with anonymous users.

## Limitations
Because SDK v4 was not designed to uphold GDPR specifications, there are some potential concerns that customers may still have with this integration.

- Attribution Source will forever be retained on the device, and reuploaded if a user opts back in.
- First Ever session open datapoint will forever be retained on the device, and reuploaded if a user opts back in.
- Advertising Identifiers will be appended to the URL's of Inbox and In-App call to actions. Customers should ensure that no links to their own webpages should take advantage of this value if a user is opted out.
- As listed in the sample code, all identifiers (including customer identifiers such as first name, last name etc.) need to be manually deleted by the customer by setting them to null, to ensure those values aren't remembered when a customer opts back in.
- Auto-Integrate is strictly disallowed.

If you have any concerns with the above limitations, please reach out to our [support team](mailto:support@localytics.com) or speak with your Mobile Engagement Consultant.