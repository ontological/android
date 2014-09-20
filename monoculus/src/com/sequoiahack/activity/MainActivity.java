package com.sequoiahack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sequoiahack.R;
import com.sequoiahack.service.LocationService;
import com.sequoiahack.service.SensorService;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startService(new Intent(SensorService.class.getName()));
    startService(new Intent(LocationService.class.getName()));
  }
  
  @Override
  protected void onDestroy() {
    stopService(new Intent(this, SensorService.class));
    stopService(new Intent(this, LocationService.class));
    super.onDestroy();
  }
}
