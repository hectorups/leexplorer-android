package com.leexplorer.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.activeandroid.ActiveAndroid;
import com.leexplorer.app.util.ArtDate;

/**
 * Created by hectormonserrate on 13/02/14.
 */
public class LeexplorerApplication extends Application {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
        ArtDate.initialize(this);

    }

    @Override
    public void onTerminate() {
        ActiveAndroid.dispose();
        super.onTerminate();
    }
}
