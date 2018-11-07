package com.taher.qatifedu;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.util.Log;

public class GCMRegistrationReceiver extends BroadcastReceiver {
	private static int NOTIFICATION_ID = 1;
	private static final String EXTRA_MESSAGE = "message";
	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "GCM";
	SharedPreferences prefs;
	int notificationNumber;
	String regid;
	Context context = null;
	 File data = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
    File file = new File(data, "token.txt");
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		this.context=context;
	    prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	    notificationNumber=prefs.getInt("notificationNumber", 0);
		Log.w("C2DM", "Registration Receiver called");
		if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			Log.w("C2DM", "Received registration ID");
			regid = intent.getStringExtra("registration_id"); 
			if(regid!=null){
			setRegistrationId(regid);
			writeToken(regid);
			new TokenLoader().execute(new String[] {regid});}}
		
		else if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			sendNotification(intent.getExtras().getString("message"),intent.getExtras().getString("id"),intent.getExtras().getString("sound"));}
	}


	private void setRegistrationId(String regId) {
		final SharedPreferences prefs = getGCMPreferences();
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	
	private SharedPreferences getGCMPreferences() {
		return context.getSharedPreferences(Constants.APPNAME, Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void sendNotification(String msg,String strID,String strSound) {
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
		Log.d("Current Volume", String.valueOf(currentVolume));

		if (currentVolume > 0) {
			// SharedPreferences prefs =
			// PreferenceManager.getDefaultSharedPreferences(context);
			if(context.getSharedPreferences("sound", Context.MODE_PRIVATE)
			.getBoolean("isSoundOn", true)){
			if(strSound!=null){
			if(strSound.equals("1"))	
			playsound(context, R.raw.sound1);
			else 
				playsound(context, R.raw.sound2);	
		}}}
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			CharSequence tickerText = msg; // ticker-text
			long when = System.currentTimeMillis();
            CharSequence contentTitle = "صفوى الإعلانية";
			CharSequence contentText = msg;
			Intent notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra(Constants.ID, Integer.parseInt(strID));
			notificationIntent.setAction("dummy_action_" + notificationNumber);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			Notification notification = new Notification(R.drawable.ic_launcher, tickerText, when);
			
			//notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notification.flags = Notification.FLAG_AUTO_CANCEL;	
	mNotificationManager.notify(notificationNumber, notification);
	SharedPreferences.Editor editor = prefs.edit();
	notificationNumber++;
	editor.putInt("notificationNumber", notificationNumber);
	editor.commit();}
	
	public static void playsound(Context mContext, int resid) {

		int maxVolume = 50;

		int currVolume = 50;

		float log1 = (float) (Math.log(maxVolume - currVolume) / Math
				.log(maxVolume));

		MediaPlayer mediaPlayer = MediaPlayer.create(mContext, resid);
		mediaPlayer.setVolume(0, 1 - log1);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.release();
			}

		});
		mediaPlayer.start();

	}
	public class TokenLoader extends AsyncTask<String, Void, String>
	{	private String response;
		InputStream is;
		@Override
		protected void onPreExecute()
		{
			}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			String url = "http://qatifedu.com/cmsQatifedu/Services/frmDeviceInfo.aspx?deviceID="+new Utility().getDeviceId(context)+"&deviceToken="+regid+"&deviceType=2&deviceName=android&deviceModel=model";
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
		protected void onPostExecute(String v)
		{}}
	
	public void writeToken(String strToken) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(strToken);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	
}
