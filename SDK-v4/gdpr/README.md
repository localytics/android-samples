# GDPR Compliant example

Sample project demonstrating the integration of Localytics for a customer supporting privacy requirements and regulations such as GDPR on SDK versions below SDK v5.1.
This app wraps a few Localytics calls to demonstrate how a an integration may prevent data from being sent to Localytics when an end-user is opted out of data collection. This code is meant as a sample in order to provide an example implementation, and is not intended as a complete solution. Specifically, this sample provides sample integration of only a few Localytics calls: `tagEvent` and `setProfileAttribute`. All other relevant Localytics API calls should be wrapped in a similar fashion. To ensure you have properly completed a privacy or GDPR compliant integration, please consult with your internal privacy or legal resources before going live.

## Prerequisites

This app integrates against SDK v4.5.1.

## Setup

1. In `LocalyticsGDPRWrapper` replace `YOUR-LOCALYTICS-APP-KEY` with your Localytics app key.
2. Build and run.

## Summary
This app is gated by an authentication screen.  It is required that the end user log into the app in order to use the underlying features.  As a result, we don't deal with anonymous users.

## Limitations
Because SDK v4 was not designed with specific tools to help support privacy requirements or regulations such as GDPR, there are some data handling factors with this integration that customers should be aware of and consider.

- Attribution Source will be retained indefinitely on the device, and re-uploaded if an end-user opts back in.
- First Ever session open datapoint will be retained indefinitely on the device, and re-uploaded if an end-user opts back in.
- Advertising Identifiers will be appended to the URL's of Inbox and In-App call to actions. Customers should consider proper handling of this identifier such as preventing links to their own webpages from using this value if a user is opted out.
- As listed in the sample code, all identifiers (including customer identifiers such as first name, last name etc.) should, if appropriate, be manually deleted by the customer (set to `null`), to prevent those values from being reset when an end-user opts back in.
- Auto-Integrate is strictly disallowed.

If you have any concerns with the above limitations, please reach out to our [support team](mailto:support@localytics.com) or speak with your Mobile Engagement Consultant.