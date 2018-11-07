package com.taher.qatifedu.utility;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class FetchApiResponse {

	static InputStream is = null;
	static String json = "";

	public String getJsonValue(final String url) {
		int timeout = 60000;
		for (int i = 0; i < 3; i++) {
			try {
				Log.e("Api-CAll---------------->", url);
				HttpGet get = new HttpGet(url);
				final String androidVersion = android.os.Build.VERSION.RELEASE;
				get.setHeader("User-Agent", "osVersion" + androidVersion);
				HttpParams params = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(params, timeout);
				HttpConnectionParams.setSoTimeout(params, timeout);
				HttpClient client = new DefaultHttpClient(params);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
				return null;
			} catch (Exception e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
				return null;
			}

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF_8"), 8);
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
					builder.append(line);
				is.close();
				json = builder.toString();
				if (json != null)
					return json;
				else {
					Log.e("Api-failed-during Get", url);
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			} catch (IOException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			} catch (Exception e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			}
		}
		return json;

	}

	public String getJsonFromApiByPOST(String url, UrlEncodedFormEntity urlEncodedFormEntity) {
		json = "";
		for (int i = 0; i < 3; i++) {
			// Making HTTP request
			try {
				Log.i("Api hit-Post->", url);
				HttpPost httpPost = new HttpPost(url);
				final String androidVersion = android.os.Build.VERSION.RELEASE;
				httpPost.setHeader("User-Agent", "osVersion=" + androidVersion);
				if (urlEncodedFormEntity != null)
					httpPost.setEntity(urlEncodedFormEntity);

				HttpParams httpParams = new BasicHttpParams();
				int some_reasonable_timeout = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
				HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
				HttpClient client = new DefaultHttpClient(httpParams);

				HttpResponse httpResponse = client.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			} catch (ClientProtocolException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			} catch (IOException e) {
				Log.e(Constants.TAG, "An exception was thrown", e);
			} catch (Exception e) {
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
				json = sb.toString();
				if (json != null) {
					Log.i("Api Response-POST->", url);
					return json;
				} else {
					Log.e("Api Failed-POST->", url);
				}
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
		}

		return json;

	}

	public XmlPullParser fetchXmlPullParserResponse(final String apiLink) throws Exception {
		// Fetch Data
		XmlPullParser fetchedData = null;
		InputStream is = null;
		Log.i("Fetching from API", "-->" + apiLink);
		try {
			is = populateInputStream(apiLink);
			fetchedData = XmlPullParserFactory.newInstance().newPullParser();
			fetchedData.setInput(is, null);
		} catch (MalformedURLException e) {
			Log.e(Constants.TAG, "An exception was thrown", e);
			return null;
		}
		return fetchedData;
	}

	private InputStream populateInputStream(final String apiLink) throws Exception {
		InputStream is;
		final URL url = new URL(apiLink);
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(25000);
		urlConnection.setReadTimeout(25000);
		final String androidVersion = android.os.Build.VERSION.RELEASE;

		urlConnection.setRequestProperty("User-Agent", "osVersion=" + androidVersion);

		urlConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
		urlConnection.connect();

		final String encoding = urlConnection.getContentEncoding();

		// create appropriate stream wrapper based on encoding type
		if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
			is = new GZIPInputStream(urlConnection.getInputStream());
		} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
			is = new InflaterInputStream(urlConnection.getInputStream(), new Inflater(true));
		} else {
			is = urlConnection.getInputStream();
		}
		return is;
	}

}
