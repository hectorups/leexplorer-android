package com.leexplorer.app.core;

import com.leexplorer.app.BuildConfig;
import com.leexplorer.app.R;
import java.util.HashMap;
import java.util.Map;

public class AppConstants {

  public static final String APP_NAME = "LEEXPLORER";

  public static final String APP_FOLDER = "leexplorer";

  public static final String LE_UUID = "3af35bf6-dffb-48d2-b060-d0f5a71096e0";

  public static final Map<String, Integer> FACILITIES_IMG_MAP = new HashMap<>();

  //public static final String API_URL = "http://10.12.34.255:1321"; // Hector 1
  //    public static final String API_URL = "http://10.0.0.2:1321"; // Hector 2
  //    public static final String SERVER_THUMBOR_URL = "10.0.0.2:8821";

  // Digital Ocean
  public static final String API_URL = "http://api.leexplorer.com:1337";
  public static final String SERVER_THUMBOR_URL = "http://images.leexplorer.com:8888";
  public static final String THUMBOR_KEY = "SBxmeZb96YXq7qf";

  public static final String BUILD_KILLED_MESSAGE = "Client update required, min build:";
  public static final String CLIENT_BUILD_HEADER_KEY = "X-LeExplorer-Client";
  public static final String CLIENT_NAME = "LeExplorer-Android";

  static {
    FACILITIES_IMG_MAP.put("accessibility", R.drawable.ic_facilities_accessibility);
    FACILITIES_IMG_MAP.put("wifi", R.drawable.ic_facilities_wifi);
    FACILITIES_IMG_MAP.put("cafe", R.drawable.ic_facilities_cafe);
  }

  public static final Map<String, String> FACILITIES_LABEL_MAP = new HashMap<String, String>();

  static {
    FACILITIES_LABEL_MAP.put("accessibility", "Wheelchair Accessible");
    FACILITIES_LABEL_MAP.put("wifi", "Wireless Internet");
    FACILITIES_LABEL_MAP.put("cafe", "Cafe");
  }


  public static final String MIXPANEL_TOKEN = "b66f535a8b703ce67e53b646b99de279";
  public static final String GOOGLE_ANALYTICS_ID = "UA-53532539-1";

  public static boolean isDebug(){
    return BuildConfig.DEBUG;
  }
}
