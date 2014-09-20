package com.hackathon.coshop;

import android.os.AsyncTask;
import android.util.Log;

public class RequestTask extends AsyncTask<String, Void, String> {

  @Override
  protected String doInBackground(String... urls) {
    try {
      java.net.URL endpointUrl = new java.net.URL(urls[0]);
      java.net.HttpURLConnection request = (java.net.HttpURLConnection) endpointUrl.openConnection();
      request.setRequestMethod("GET");
      request.connect();
      java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(
          request.getInputStream()));
      StringBuilder response = new StringBuilder();
      String line = null;
      while ((line = rd.readLine()) != null) {
        response.append(line + '\n');
      }
      request.disconnect();
      rd.close();
      Log.i("coshop", "Response = " + response);
      return response.toString();
    } catch (Exception e) {
      Log.e("coshop", "Exception caught = " + e.getMessage());
    }
    return null;
  }
}
