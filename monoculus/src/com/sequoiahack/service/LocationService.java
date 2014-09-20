package com.sequoiahack.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService extends Service implements LocationListener,
    GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

  private LocationClient mLocationClient;
  private LocationRequest mLocationRequest;

  public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
  private static final long UPDATE_INTERVAL = 5000; // ms
  private static final long FASTEST_INTERVAL = 2000; // ms

  @Override
  public void onCreate() {
    super.onCreate();
    mLocationClient = new LocationClient(this, this, this);
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mLocationClient.connect();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mLocationClient.disconnect();
  }

  @Override
  public void onConnected(Bundle arg0) {
    Log.d("onConnected", "Location service connected.");
    mLocationClient.requestLocationUpdates(mLocationRequest, this);
    getAddress();
  }

  @Override
  public void onDisconnected() {
    Log.d("onDisconnected", "Location service disconnected.");
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.d("onConnectionFailed", "Unable to connect to Location service.");
  }

  @Override
  public void onLocationChanged(Location location) {
    String msg = "Updated Location: " + Double.toString(location.getLatitude()) + ","
        + Double.toString(location.getLongitude());
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    getAddress();
  }

  public void getAddress() {
    Location mLocation = mLocationClient.getLastLocation();
    //new GetAddressTask(this).execute(mLocation);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}