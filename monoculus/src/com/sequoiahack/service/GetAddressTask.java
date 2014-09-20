package com.sequoiahack.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sequoiahack.R;

/**
 * A subclass of AsyncTask that calls getFromLocation() in the background.
 */
public class GetAddressTask extends AsyncTask<Location, Void, String> {
  Context mContext;
  private View view;
  
  public GetAddressTask(Context context, View view) {
    super();
    mContext = context;
    this.view = view;
  }

  /**
   * Get a Geocoder instance, get the latitude and longitude look up the
   * address, and return it.
   * 
   * @params params One or more Location objects
   * @return A string containing the address of the current location, or an
   *         empty string if no address can be found, or an error message
   */
  @Override
  protected String doInBackground(Location... params) {
    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
    // Get the current location from the input parameter list
    Location loc = params[0];
    if (loc == null)
      return "can't get location";
    List<Address> addresses = null;
    try {
      addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
    } catch (IOException e1) {
      Log.e("GetAddressTask", "IO Exception in getFromLocation()");
      e1.printStackTrace();
      return ("IO Exception trying to get address");
    } catch (IllegalArgumentException e2) {
      String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) + " , "
          + Double.toString(loc.getLongitude()) + " passed to address service";
      Log.e("GetAddressTask", errorString);
      e2.printStackTrace();
      return errorString;
    }

    // If the reverse geocode returned an address
    if (addresses != null && addresses.size() > 0) {
      Address address = addresses.get(0);
      String addressText = String.format("%s, %s, %s",
          address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(),
          address.getCountryName());
      return addressText;
    } else {
      return "No address found";
    }
  }

  /**
   * A method that's called once doInBackground() completes. Turn off the
   * indeterminate activity indicator and set the text of the UI element that
   * shows the address. If the lookup failed, display the error message.
   */
  @Override
  protected void onPostExecute(String address) {
    TextView addressView = (TextView)this.view.findViewById(R.id.address);
    addressView.setText(address);
  }
}
