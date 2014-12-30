package com.leexplorer.app.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.util.PreferenceUtils;

public class LocationService extends Service
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private static final String TAG = "com.leexplorer.app.services.LocationService";
  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // meters
  private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
  private GoogleApiClient googleApiClient;
  private LocationRequest locationRequest;
  private Location location;
  IBinder binder = new LocalBinder();
  private static boolean isRunning;

  @Override public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate");
    initializeClient();
    isRunning = true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand " + startId + ": " + intent);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    googleApiClient.disconnect();
    isRunning = false;
    super.onDestroy();
  }

  public void initializeClient() {
    googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();

    googleApiClient.connect();
  }

  public Location getLocation() {
    return this.location;
  }

  @Override
  public void onConnected(Bundle bundle) {
    locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    locationRequest.setInterval(MIN_TIME_BW_UPDATES);
    locationRequest.setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);

    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
        googleApiClient);
    if (lastLocation != null) {
      Log.d(TAG, "last location " + lastLocation.getLatitude() + ": " + lastLocation.getLongitude());
      saveLocation(lastLocation);
    }

    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
        this);
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
  }

  @Override
  public void onLocationChanged(Location location) {
    Log.d(TAG, "location update " + location.getLatitude() + ": " + location.getLongitude());
    saveLocation(location);
  }

  private void saveLocation(Location location) {
    this.location = location;
    PreferenceUtils.putDouble(this, AppConstants.KEY_LAST_KNOWN_LATITUDE, location.getLatitude());
    PreferenceUtils.putDouble(this, AppConstants.KEY_LAST_KNOWN_LONGITUDE, location.getLongitude());
  }

  public static boolean isRunning() {
    return isRunning;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  public class LocalBinder extends Binder {
    public LocationService getServerInstance() {
      return LocationService.this;
    }
  }
}
