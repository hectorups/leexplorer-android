package com.leexplorer.app;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by hectormonserrate on 13/02/14.
 */
public class LeexplorerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
