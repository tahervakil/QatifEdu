package com.taher.qatifedu.utility;

import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
public class HttpUtils {
	private static final String UTF8 = "UTF-8";
	
	public static String loafd(String urlString, int timeout, String method) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeout);
		InputStream istream = conn.getInputStream();
		String result = convertStreamToUTF8String(istream);
		return result;
	}
	
	public static InputStream loadStream(String urlString, int timeout, String method) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeout);
		InputStream istream = conn.getInputStream();
		return istream;
	}
	
	public static void writeToStream(String urlString, int timeout, String method, OutputStream ostream) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeout);
		InputStream istream = conn.getInputStream();
		copyStream(istream, ostream, 4096);
	}
	
	public static void copyStream(InputStream istream ,OutputStream ostream, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int readed = 0;
		do {
			readed = istream.read(buffer);
			if (readed > 0)
				ostream.write(buffer, 0, readed);
		} while (readed > 0);
	}
	
	public static String convertStreamToUTF8String(InputStream stream) throws IOException {
		String result = "";
		StringBuilder sb = new StringBuilder();
		try {
			InputStreamReader reader = new InputStreamReader(stream, UTF8);
			char[] buffer = new char[4096];
			int readedChars = 0;
			while (readedChars != -1) {
				readedChars = reader.read(buffer);
				if (readedChars > 0)
					sb.append(buffer, 0, readedChars);
			}
			result = sb.toString();
		} catch (UnsupportedEncodingException e) {
			Log.e(Constants.TAG, "An exception was thrown", e);
		}
		return result;
	}
}
