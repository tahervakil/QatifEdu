package com.taher.qatifedu.fragments;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.taher.qatifedu.R;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.Utility;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

public class InternalBrowser extends /*Sherlock*/Fragment  {
	WebView webView = null;
	View myFragmentView;
	View screener;
	private  /*SherlockFragment*/AppCompatActivity act;
	private String name;
	private int type;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		 act=/*getActionBar()*/(AppCompatActivity)getActivity();
		 if (myFragmentView == null) {
		 myFragmentView = inflater.inflate(R.layout.internalbrowser, container, false);
		 screener = myFragmentView.findViewById(R.id.screener_pd);
		 Bundle bundle = this.getArguments();
		 name=bundle.getString(Constants.NAME);
		 type=bundle.getInt(Constants.TYPE);
		 ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
		 screener.setVisibility(View.VISIBLE);
		 webView = (WebView) myFragmentView.findViewById(R.id.wv_internal);
		 webView.getSettings().setJavaScriptEnabled(true);
		 webView.getSettings().setBuiltInZoomControls(true);
		 webView.getSettings().setDefaultTextEncodingName("utf-8");
		 webView.getSettings().setAppCacheEnabled(true);
		 webView.getSettings().setAllowFileAccess(true);

		 webView.setWebViewClient(new WebViewClient() {
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		            if( URLUtil.isNetworkUrl(url) ) {
		                return false;
		            }
		            if (appInstalledOrNot(url)) {
		                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		                startActivity( intent );
		            } else {
		                // do something if app is not installed
		            }
		            return true;
		        }
		        @Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					screener.setVisibility(View.GONE);
				}
		    });
		
		 
		 
		
			webView.setOnKeyListener(new OnKeyListener(){

	            public boolean onKey(View v, int keyCode, KeyEvent event) {
	                  if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	                        handler.sendEmptyMessage(1);
	                        return true;
	                    }
	                    return false; }});

			if (Utility.isConnected(act)){
				String url=bundle.getString("url");
				if(url.startsWith("http"))
				webView.loadUrl(url);
				else 
					webView.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null);}
			
			else
					Utility.showToast(act, getString(R.string.alert_need_internet_connection)); }
		 else 
			 ((ViewGroup)myFragmentView.getParent()).removeView(myFragmentView);
		return myFragmentView;}
	
	private Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message message) {
	        switch (message.what) {
	            case 1:{
	                webViewGoBack();
	            }break;
	        }
	    }
	};
	 private void webViewGoBack(){
		 webView.goBack();
	    }

	 @Override
	  public void onResume(){
		((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
		((ImageButton) act.getSupportActionBar().getCustomView().findViewById(R.id.btn_refreash)).setVisibility(View.GONE);
		super.onResume();
	  }
	 
	 private boolean appInstalledOrNot(String uri) {
	        PackageManager pm = act.getPackageManager();
	        try {
	            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	            return true;
	        } catch (PackageManager.NameNotFoundException e) {
	        }

	        return false;
	    }
}
