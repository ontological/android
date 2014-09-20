package com.hackathon.coshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSmsService extends Service {

  String REST_ENDPOINT = "http://coshop-router.herokuapp.com/peek";

  public SendSmsService() {
  }

  public void sendLongSMS() {
    try {
      String response = new RequestTask().execute(REST_ENDPOINT).get();
      if (response != null) {
        SmsManager smsManager = SmsManager.getDefault();
        List<SendDetails> senderList = getSenderList(response);
        int sz = senderList.size();
        for (int i = 0; i < sz; i++) {
          SendDetails details = senderList.get(i);
          if (details.phone_num == null || details.phone_num.equals(""))
            continue;
          details.msg = details.name + " sent this message via CoShop:: " + details.msg;
          details.phone_num = "+91" + details.phone_num.substring(details.phone_num.length() - 10);
          ArrayList<String> parts = smsManager.divideMessage(details.msg);
          smsManager.sendMultipartTextMessage(details.phone_num, null, parts, null, null);
          // smsManager.sendMultipartTextMessage("5554", null, parts, null,
          // null);
          ContentValues values = new ContentValues();
          values.put("address", details.phone_num);
          values.put("body", details.msg);
          getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("coshop", "SendSMS : Received start id " + startId + ": " + intent);
    return START_STICKY;
  }

  private Timer timer;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("coshop", "creating the SendSMS service");
    timer = new Timer("SendSmsTimer");
    timer.schedule(smsTask, 100L, 6 * 1000L);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i("coshop", "destroying the SendSMS service");
    timer.cancel();
    timer = null;
  }

  private TimerTask smsTask = new TimerTask() {
    @Override
    public void run() {
      Log.i("coshop", "Timer task is working");
      sendLongSMS();
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private List<SendDetails> getSenderList(String response) {
    List<SendDetails> send_details_list = null;
    try {
      JSONArray json_arr = new JSONArray(response);
      int len = json_arr.length();
      send_details_list = new ArrayList<SendSmsService.SendDetails>();
      for (int i = 0; i < len; i++) {
        JSONObject sender_detail = json_arr.getJSONObject(i);
        SendDetails s = new SendDetails(sender_detail);
        send_details_list.add(s);
      }
    } catch (JSONException e) {
      Log.e("coshop", "Invalid JSON from CoShop API" + response);
    }
    return send_details_list;
  }

  private class SendDetails {
    public String phone_num;
    public String name;
    public String msg;

    public SendDetails(JSONObject sender_detail) {
      try {
        this.phone_num = sender_detail.getString("to");
        this.name = sender_detail.getString("user");
        this.msg = sender_detail.getString("msg");
      } catch (JSONException e) {
        Log.e("coshop", "Invalid JSON from CoShop API" + e);
      }
    }
  }

}
