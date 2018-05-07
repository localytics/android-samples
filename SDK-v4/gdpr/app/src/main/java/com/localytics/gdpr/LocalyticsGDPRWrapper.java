package com.localytics.gdpr;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class acts as a wrapper around the Localytics implementation to ensure that no data can be
 * passed to the Localytics system if a user has opted out of data collection.
 *
 * This implementation expects that every time the app is created, the opted in state of the user is
 * checked against a backend source of truth.  As a result, the opted in state will be stored as an
 * instance variable, and persistant storage will not be required.
 */
public class LocalyticsGDPRWrapper {

    private static LocalyticsGDPRWrapper INSTANCE = null;

    /**
     * A boolean representing the user's current opt out status
     */
    private boolean optedOut = false;

    /**
     * A boolean representing whether or not datapoints can immediately be passed to Localytics.
     * This value should be true when the current user's opt in status is unknown by the app.
     */
    private boolean queueDatapoints = false;

    /**
     * A queue of event datapoints that have been triggered by the app while the user's opt in state is unknown
     */
    private List<Event> eventQueue = new ArrayList<Event>();

    /**
     * A queue of profile datapoints that have been triggered by the app while the user's opt in state is unknown
     */
    private List<Profile> profileQueue = new ArrayList<Profile>();

    static LocalyticsGDPRWrapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalyticsGDPRWrapper();
        }
        return INSTANCE;
    }

    private LocalyticsGDPRWrapper() {
        //private constructor to ensure getInstance is called.
    }

    void setLoggingEnabled(boolean loggingEnabled) {
        Localytics.setLoggingEnabled(loggingEnabled);
    }

    //    Integration/Session tracking methods.

    public void autoIntegrate(Application application) {
        Localytics.autoIntegrate(application, "YOUR-LOCALYTICS-APP-KEY");
    }

    public void onNewIntent(Activity activity, Intent intent) {
        Localytics.onNewIntent(activity, intent);
    }

    public void pauseDataUploading(boolean pause) {
        queueDatapoints = pause;
        if (!pause) {
            for (Event event : eventQueue) {
                event.tagEvent();
            }
            for (Profile profile : profileQueue) {
                profile.tagProfile();
            }
        }
    }

    public void setLocationMonitoringEnabled(boolean locationMonitoringEnabled) {
        Localytics.setLocationMonitoringEnabled(locationMonitoringEnabled);
    }

    //    Getters

    public String getCustomerId() {
        return Localytics.getCustomerId();
    }

    // User login, logout, opt in, opt out

    public void setPrivacyOptedOut(boolean privacyOptedOut) {
        if (optedOut == privacyOptedOut) {
            return;
        }
        updateOptedOutState(privacyOptedOut);
    }

    public void logIn(String customerId, boolean optedOut) {
        Localytics.setCustomerId(customerId);
        updateOptedOutState(optedOut);
    }

    public void logout() {
        Localytics.setCustomerId(null);
        updateOptedOutState(false);
    }

    private void updateOptedOutState(boolean optedOut) {
        this.optedOut = optedOut;
        Localytics.setProfileAttribute("privacy_delete", optedOut ? 1 : 0);
        Localytics.setOptedOut(optedOut);
        if (optedOut) {
            //Make sure to delete all identifiers that you have set.
            Localytics.setIdentifier("your defined identifiers", null);
//            If you are using places, we suggest shutting it down when a user opts out.
//            Localytics.setLocationMonitoringEnabled(false);
            eventQueue.clear();
            profileQueue.clear();
        } else {
//            If you are using places, make sure to re-enable places when a user opts back in.
//            Localytics.setLocationMonitoringEnabled(true);
        }
        Localytics.upload();
    }

    // Example of implementing tagEvent and setProfileAttribute.
    // Any additional event or profile methods overridden should follow a similar protocol.

    public void tagEvent(String eventName, HashMap<String, String> attributes, long clv) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Event(eventName, attributes, clv));
        } else {
            Localytics.tagEvent(eventName, attributes, clv);
        }
    }

    public void setProfileAttribute(String attribute, String value, Localytics.ProfileScope scope) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            profileQueue.add(new Profile(attribute, value, scope));
        } else {
            Localytics.setProfileAttribute(attribute, value, scope);
        }

    }

    private final class Event {

        private final String eventName;
        private final HashMap<String, String> attributes;
        private final long clv;

        Event(String eventName, HashMap<String, String> attributes, long clv) {
            this.eventName = eventName;
            this.attributes = attributes;
            this.clv = clv;
        }

        void tagEvent() {
            Localytics.tagEvent(eventName, attributes, clv);
        }
    }

    private final class Profile {

        private final String attribute;
        private final String value;
        private final Localytics.ProfileScope scope;

        Profile(String attribute, String value, Localytics.ProfileScope scope) {
            this.attribute = attribute;
            this.value = value;
            this.scope = scope;
        }


        // For a more complete implementation, you will need to distinguish between various
        // Profile calls (eg. setProfileAttribute, incrementProfileAttribute...)
        void tagProfile() {
            Localytics.setProfileAttribute(attribute, value, scope);
        }
    }
}
