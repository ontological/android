package com.hackathon.coshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ReceiveSMS extends BroadcastReceiver {

  String REST_ENDPOINT = "http://coshop-router.herokuapp.com/save?type=mobile&user=%s&session_id=&msg=%s&to=&url=&flag=1";

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
      Log.i("coshop", "SMS received.");
      Bundle bundle = intent.getExtras(); // Get the SMS message passed
      SmsMessage[] recievedMsgs = null;
      String sender = "";
      String msgBody = "";
      String encoded_msg = "";
      if (bundle != null) {
        // Retrieve the SMS message received
        try {
          Object[] pdus = (Object[]) bundle.get("pdus");
          recievedMsgs = new SmsMessage[pdus.length];
          for (int i = 0; i < recievedMsgs.length; i++) {
            recievedMsgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            if (i == 0) {
              // Get the sender address/phone number
              sender = recievedMsgs[i].getOriginatingAddress();
            }
            msgBody += recievedMsgs[i].getMessageBody().toString();
          }
          sender = sender.substring(sender.length() - 10);
          String str = sender + " : " + msgBody;
          Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
          Log.d("coshop", "SMSReceiver = " + str);
          encoded_msg = java.net.URLEncoder.encode(msgBody, "UTF-8");
          String endpoint = String.format(REST_ENDPOINT, sender, encoded_msg);
          int num_tries = 0;
          while (num_tries < 3) {
            String ret = new RequestTask().execute(endpoint).get();
            if (ret != null)
              break;
            else {
              num_tries++;
              Thread.sleep(50);
            }
          }
          // send a broadcast intent to update the SMS received in the activity
          Intent broadcastIntent = new Intent();
          broadcastIntent.setAction("SMS_RECEIVED_ACTION");
          broadcastIntent.putExtra("sms", str);
          context.sendBroadcast(broadcastIntent);
        } catch (Exception e) {
          Log.e("coshop", "Exception caught = " + e.getMessage());
        }
      }
    }
  }

}
