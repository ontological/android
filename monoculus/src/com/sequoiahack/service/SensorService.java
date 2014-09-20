package com.sequoiahack.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {

  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private Sensor mMagnetometer;
  private String direction;

  public SensorService() {

  }

  @Override
  public void onCreate() {
    super.onCreate();
    initialiseSensor();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mSensorManager.unregisterListener(this);
  }

  public void initialiseSensor() {
    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  public String getDirection() {
    return direction;
  }

  float[] mGravity;
  float[] mGeomagnetic;

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      mGravity = event.values;
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      mGeomagnetic = event.values;
    if (mGravity != null && mGeomagnetic != null) {
      float R[] = new float[9];
      float I[] = new float[9];
      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
      if (success) {
        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        float yaw = orientation[0];
        float degree = yaw * 360 / (2 * 3.14159f);
        Log.d("onOrientationChange", "Current Degree : " + degree);
        if (degree < 0) {
          degree += 360;
        }
        this.direction = degToDirection(degree);
      }
    }
  }

  public static String degToDirection(float num) {
    int val = (int) ((num / 45) + .5);
    String arr[] = new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
    String direction = arr[(val % 8)];
    System.out.println(direction);
    return direction;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
