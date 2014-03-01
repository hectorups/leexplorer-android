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

    static {
        FACILITIES_IMG_MAP.put("wheelchair accessible", R.drawable.ic_facilities_accessibility);
        FACILITIES_IMG_MAP.put("wifi available", R.drawable.ic_facilities_wifi);
        FACILITIES_IMG_MAP.put("cafe", R.drawable.ic_facilities_cafe);
    }

}
