package com.taher.qatifedu.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taher.qatifedu.MainActivity;
import com.taher.qatifedu.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class MessagingService extends FirebaseMessagingService {

  String id = "my_channel_01";
  int notifyId = 1;
  private static String TAG = "MESSAGING_SERVICE";
  String token = null;
  private static String uniqueID = null;
  private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Log.i(TAG, "This is the message recieved " + remoteMessage.getNotification().getBody());
    showNotification(remoteMessage.getNotification().getBody());
  }

  @Override
  public void onNewToken(String s) {
    super.onNewToken(s);
    Log.i(TAG, "New Token Received " + s);
    token = s;
    getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    new TokenLoader().execute();
  }

  public void showNotification(String message) {
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
    Notification notification =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("تعليم القطيف")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setChannelId(id)
            .build();

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      /* Create or update. */
      NotificationChannel channel =
          new NotificationChannel(
              "my_channel_01",
              "Channel human readable title",
              NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }
    notificationManager.notify(notifyId, notification);
  }

  public class TokenLoader extends AsyncTask<String, Void, String> {
    private String response;
    InputStream is;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... arg0) {
      final TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

      String url =
          "http://qatifedu.com/cmsQatifedu/Services/frmDeviceInfo.aspx?deviceID="
              + id(getApplicationContext())
              + "&deviceToken="
              + token
              + "&deviceType=2&deviceName=android&deviceModel=model";
      HttpGet httpGet = new HttpGet(url);
      final String androidVersion = android.os.Build.VERSION.RELEASE;
      httpGet.setHeader("User-Agent", "osVersion=" + androidVersion);

      HttpParams httpParams = new BasicHttpParams();
      int some_reasonable_timeout = 30000;
      HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
      HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
      HttpClient client = new DefaultHttpClient(httpParams);

      HttpResponse httpResponse = null;
      try {
        httpResponse = client.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        is = httpEntity.getContent();
      } catch (ClientProtocolException e) {
        Log.e(Constants.TAG, "An exception was thrown", e);
      } catch (IOException e) {
        Log.e(Constants.TAG, "An exception was thrown", e);
      }

      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        response = sb.toString();
        if (response != null) {
          Log.i("Api Response-GET->", url);
        } else {
          Log.e("Api Failed-GET->", url);
        }
        return response;
      } catch (Exception e) {
        Log.e("Buffer Error", "Error converting result " + e.toString());
      }
      return "";
    }

    @Override
    protected void onPostExecute(String v) {}
  }

  public synchronized static String id(Context context) {
    if (uniqueID == null) {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
              PREF_UNIQUE_ID, Context.MODE_PRIVATE);
      uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
      if (uniqueID == null) {
        uniqueID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_UNIQUE_ID, uniqueID);
        editor.commit();
      }
    }
    return uniqueID;
  }
}
