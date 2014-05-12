package com.leexplorer.app.util;

import com.leexplorer.app.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by deepakdhiman on 2/25/14.
 */
public class AppConstants {

  public static final String APP_NAME = "LEEXPLORER";

  public static final Map<String, Integer> FACILITIES_IMG_MAP = new HashMap<String, Integer>();

  //public static final String API_URL = "http://10.12.34.255:1321"; // Hector 1
  //    public static final String API_URL = "http://10.0.0.2:1321"; // Hector 2
  //    public static final String SERVER_THUMBOR_URL = "10.0.0.2:8821";

  // Digital Ocean
  public static final String API_URL = "http://107.170.66.79:1337";
  public static final String SERVER_THUMBOR_URL = "http://107.170.66.79:8888";

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
}
