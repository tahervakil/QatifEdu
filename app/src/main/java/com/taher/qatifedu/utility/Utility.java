package com.taher.qatifedu.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		boolean connected = ni != null && ni.isConnected();
		return connected;
	}

	

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public void alert(String message, Activity activity,android.content.DialogInterface.OnClickListener onclick) {
		AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setMessage(message);
		bld.setNeutralButton("OK", onclick);
		bld.create().show();
	}

	

	
	public String getDeviceId(Context context) {

		try {
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			if (deviceid == null)
				deviceid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			// Convert Device ID into SHA-1
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(deviceid.getBytes("iso-8859-1"), 0, deviceid.length());
			byte[] sha1hash = md.digest();

			return convertToHex(sha1hash);

		} catch (NoSuchAlgorithmException e) {
			Log.e(Constants.TAG, "An exception was thrown", e);
		} catch (Exception e) {
			Log.e(Constants.TAG, "An exception was thrown", e);
		}

		return "";
	}

	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {
		    return capitalize(manufacturer) + " " + model;
		  }
		}


		private String capitalize(String s) {
		  if (s == null || s.length() == 0) {
		    return "";
		  }
		  char first = s.charAt(0);
		  if (Character.isUpperCase(first)) {
		    return s;
		  } else {
		    return Character.toUpperCase(first) + s.substring(1);
		  }
		}
	/**
	 * Convert a byte[] to hexadecimal.
	 * 
	 * @param data
	 * @return
	 */
	private String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	
	
	
	
	public String readFile(InputStream is) {
		Log.e("Vinay", "Reading File");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer linecomp = new StringBuffer();
		try {
			String line;

			// line = new String(lineReader.readLine()); // read and ignore the
			while ((line = reader.readLine()) != null) {
				linecomp = linecomp.append(line);
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, "An exception was thrown", e);
		}
		return linecomp.toString();

	}
	
	
	 public static void sendMail(Activity act, String mailID, String mailSubject, String mailBody) {

			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
			intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { mailID });
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			act.startActivityForResult(Intent.createChooser(intent, "Send mail.."),1);

		}
	 
	 public static String convertDate(long dateInMilliseconds,String dateFormat) {
		 SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
         Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(dateInMilliseconds);
	     return formatter.format(calendar.getTime());

		}
	 
	 public static Boolean  isExpired(String strToDate) {
		 boolean isExpired = false;
		 try{
		 Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(strToDate);
		 long toDateAsTimestamp = toDate.getTime();
		 long currentTimestamp = System.currentTimeMillis();
		 long getRidOfTime = 1000 * 60 * 60 * 24;
		 long toDateAsTimestampWithoutTime = toDateAsTimestamp / getRidOfTime;
		 long currentTimestampWithoutTime = currentTimestamp / getRidOfTime;

		 if (toDateAsTimestampWithoutTime >= currentTimestampWithoutTime) {
			 isExpired=false;
		 } else {
			 isExpired=true;
		 }}catch(Exception ex){}
		    return isExpired;
		}
}
