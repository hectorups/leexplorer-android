package com.leexplorer.app.core;

import android.text.TextUtils;
import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.R;
import java.util.HashMap;
import java.util.Map;

public class AppConstants {

  public static final String APP_FOLDER = "leexplorer";

  public static final String LE_UUID = "9133edc4-a87c-5529-befa-4f75f31a45d4";

  public static final Map<String, Integer> FACILITIES_IMG_MAP = new HashMap<>();

  // Network
  public static final int CONNECT_TIMEOUT = 2;
  public static final int READ_TIMEOUT = 35;
  public static final int NETWORK_CACHE = 10 * 1024 * 1024;
  public static final String HMAC_KEY = "rAs12345ti";
  public static final String PROXY = null; //"10.0.0.7";
  public static final int PROXY_PORT = 8887;

  // Digital Ocean
  public static final String API_URL = "https://leexplorer.herokuapp.com";
  public static final String STAGING_API_URL = "http://leexplorer.herokuapp.com";

  public static final String CLOUDINARY_CLOUD_NAME = "leexplorer";

  public static final String BUILD_KILLED_MESSAGE = "Client update required, min build:";
  public static final String CLIENT_BUILD_HEADER_KEY = "X-LeExplorer-Client";
  public static final String CLIENT_HMAC_KEY = "X-LeExplorer-Hmac";
  public static final String CLIENT_NAME = "LeExplorer-Android";

  public static final int MIN_METRES_FOR_AUTOPLAY = 20;
  public static final int MILSEC_ARTWORK_REFRESH = 30000;

  public static final String FEEDBACK_EMAIL = "hectorups@gmail.com";

  public static final String[] ALLOWED_SHARED_PACKAGE_NAMES = {
      "facebook", "twitter", "mail", "com.google.android.gm", "instagram", "pinterest", "yahoo",
      "whatsapp"
  };

  static {
    FACILITIES_IMG_MAP.put("accessibility", R.drawable.ic_facilities_accessibility);
    FACILITIES_IMG_MAP.put("wifi", R.drawable.ic_facilities_wifi);
    FACILITIES_IMG_MAP.put("cafe", R.drawable.ic_facilities_cafe);
  }

  public static final Map<String, String> FACILITIES_LABEL_MAP = new HashMap<>();

  static {
    FACILITIES_LABEL_MAP.put("accessibility", "Wheelchair Accessible");
    FACILITIES_LABEL_MAP.put("wifi", "Wireless Internet");
    FACILITIES_LABEL_MAP.put("cafe", "Cafe");
  }

  public static final String MIXPANEL_TOKEN = "b66f535a8b703ce67e53b646b99de279";
  public static final String GOOGLE_ANALYTICS_ID = "UA-53532539-1";

  public static String getEndpoint() {
    return isProduction() ? API_URL : STAGING_API_URL;
  }

  public static boolean isDebug() {
    return !isProduction();
  }

  public static boolean isProduction() {
    if (!TextUtils.isEmpty(BuildConfig.ENVIRONMENT)) {
      return BuildConfig.ENVIRONMENT.contentEquals("production");
    } else {
      return !BuildConfig.DEBUG;
    }
  }
}
