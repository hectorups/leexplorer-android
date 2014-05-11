package com.leexplorer.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.leexplorer.app.util.ArtDate;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

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

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
        Crashlytics.start(this);

        ActiveAndroid.initialize(this);
        ArtDate.initialize(this);

    }

    @Override
    public void onTerminate() {
        ActiveAndroid.dispose();
        super.onTerminate();
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new LeexplorerModule(this));
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}
