package com.taher.qatifedu.utility;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;

public class GoogleAnalyticsApplication extends Application {

  private Tracker tracker;

  public synchronized Tracker getDefaultTracker() {
    if (tracker == null) {
      GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
      tracker = googleAnalytics.newTracker(R.xml.analytics_tracker);
    }

    return tracker;
  }
}
