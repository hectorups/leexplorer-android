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
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = LeexplorerApplication.this;

        ActiveAndroid.initialize(this);
        ArtDate.initialize(this);

    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) LeexplorerApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
