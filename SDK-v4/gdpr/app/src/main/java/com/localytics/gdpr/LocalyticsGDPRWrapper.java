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
 * instance variable, and persistent storage will not be required.
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
    private List<Runnable> eventQueue = new ArrayList<>();

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
        throw new RuntimeException("You can't use autoIntegrate while supporting GDPR");
    }

    public void integrate(Context context) {
        Localytics.integrate(context, "YOUR-LOCALYTICS-APP-KEY");
    }

    public void onNewIntent(final Activity activity, final Intent intent) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.onNewIntent(activity, intent);
                }
            });
        } else {
            Localytics.onNewIntent(activity, intent);
        }
    }

    public void openSession() {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.openSession();
                }
            });
        } else {
            Localytics.openSession();
        }
    }

    public void closeSession() {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.closeSession();
                }
            });
        } else {
            Localytics.closeSession();
        }
    }

    public void onActivityResume(final Activity activity) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.onActivityResume(activity);
                }
            });
        } else {
            Localytics.onActivityResume(activity);
        }
    }

    public void onActivityPause(final Activity activity) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.onActivityPause(activity);
                }
            });
        } else {
            Localytics.onActivityPause(activity);
        }
    }

    public void pauseDataUploading(boolean pause) {
        queueDatapoints = pause;
        if (!pause) {
            for (Runnable event : eventQueue) {
                event.run();
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
        } else {
//            If you are using places, make sure to re-enable places when a user opts back in.
//            Localytics.setLocationMonitoringEnabled(true);
        }
        Localytics.upload();
    }

    // Example of implementing tagEvent and setProfileAttribute.
    // Any additional event or profile methods overridden should follow a similar protocol.

    public void tagEvent(final String eventName, final HashMap<String, String> attributes, final long clv) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.tagEvent(eventName, attributes, clv);
                }
            });
        } else {
            Localytics.tagEvent(eventName, attributes, clv);
        }
    }

    public void setProfileAttribute(final String attribute, final String value, final Localytics.ProfileScope scope) {
        if (optedOut) {
            return;
        }

        if (queueDatapoints) {
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    Localytics.setProfileAttribute(attribute, value, scope);
                }
            });
        } else {
            Localytics.setProfileAttribute(attribute, value, scope);
        }

    }

}
