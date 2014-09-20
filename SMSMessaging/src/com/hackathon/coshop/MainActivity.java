package com.hackathon.coshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onStart(View view) {
    // startService(new Intent(this, SendSmsService.class));
    Toast.makeText(getApplicationContext(), "Launching the SMS service!", Toast.LENGTH_LONG).show();
    startService(new Intent(SendSmsService.class.getName()));
  }

  public void onStop(View view) {
    Toast.makeText(this, "Stopping the SMS service", Toast.LENGTH_SHORT).show();
    stopService(new Intent(this, SendSmsService.class));
  }

}
