package com.leexplorer.app.core;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class EventReporter {
  MixpanelAPI mixpanel;
  Tracker gaTracker;

  private static final String TAG = "eventreporter";

  private static final String KEY_BUILD_NUMBER = "build_number";
  private static final String KEY_DEBUG = "debug_mode";

  // USER EVENTS
  private static final String EVENT_ARTWORK_AUDIO_PLAYED = "arwork_audio_played";
  private static final String EVENT_GALLERY_DISCOVERED = "gallery_discovered";
  private static final String EVENT_GALLERY_DOWNLOADED = "gallery_downloaded";

  // EVENT ATTRS
  private static final String ATTR_ARTWORK_MAC = "artwork_mac";
  private static final String ATTR_ARTWORK_NAME = "artwork_name";
  private static final String ATTR_ARTWORK_ID = "artwork_mac";
  private static final String ATTR_GALLERY_NAME = "gallery_name";
  private static final String ATTR_GALLERY_ID = "gallery_name";

  public EventReporter(Context context) {
    // Crashalytics
    Crashlytics.setInt(KEY_BUILD_NUMBER, BuildConfig.VERSION_CODE);
    Crashlytics.setBool(KEY_DEBUG, BuildConfig.DEBUG);

    // Mixpanel
    mixpanel = MixpanelAPI.getInstance(context, AppConstants.MIXPANEL_TOKEN);

    // GA
    gaTracker = GoogleAnalytics.getInstance(context).newTracker(AppConstants.GOOGLE_ANALYTICS_ID);
    gaTracker.enableAutoActivityTracking(true);
  }

  private void logUserEvent(final String event, final Map<String, String> attrs) {
    Log.d(TAG, "UserEvent:" + event);
    Crashlytics.log("UserEvent:" + event);
    mixpanel.track(event, mapToJson(attrs));
    gaTracker.send(MapBuilder.createEvent("event", event, null, null).build());
  }

  private JSONObject mapToJson(Map<String, String> attrs) {
    JSONObject json = new JSONObject();

    try {
      for (Map.Entry<String, String> entry : attrs.entrySet()) {
        json.put(entry.getKey(), entry.getValue());
      }
    } catch (JSONException e) {
      Crashlytics.logException(e);
    }

    return json;
  }

  public void logException(String message) {
    logException(new NonFatalException(message));
  }

  public void logException(Throwable exception) {
    Crashlytics.logException(exception);
  }

  public void flush() {
    mixpanel.flush();
  }

  public void artworkAudioPlayed(Artwork artwork) {
    Map<String, String> attrs = new HashMap<>();
    attrs.put(ATTR_ARTWORK_ID, artwork.getArtworkId());
    attrs.put(ATTR_ARTWORK_MAC, artwork.getMajorminor());
    attrs.put(ATTR_ARTWORK_NAME, artwork.getMajorminor());
    attrs.put(ATTR_GALLERY_ID, artwork.getGalleryId());
    logUserEvent(EVENT_ARTWORK_AUDIO_PLAYED, attrs);
  }

  public void galleryDiscovered(Gallery gallery) {
    Map<String, String> attrs = new HashMap<>();
    attrs.put(ATTR_GALLERY_ID, gallery.getGalleryId());
    attrs.put(ATTR_GALLERY_NAME, gallery.getGalleryId());
    logUserEvent(EVENT_GALLERY_DISCOVERED, attrs);
  }

  public void galleryDownloaded(Gallery gallery) {
    Map<String, String> attrs = new HashMap<>();
    attrs.put(ATTR_GALLERY_ID, gallery.getGalleryId());
    attrs.put(ATTR_GALLERY_NAME, gallery.getGalleryId());
    logUserEvent(EVENT_GALLERY_DOWNLOADED, attrs);
  }

  public void screenViewed(String tag) {
    gaTracker.setScreenName(tag);
    gaTracker.send(new HitBuilders.AppViewBuilder().build());
  }
}
