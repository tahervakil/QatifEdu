package com.taher.qatifedu;

import com.taher.qatifedu.R;
import com.taher.qatifedu.entity.Sponsor_Entity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.TextView;

public class SponsorDetailsActivity extends Activity {
  private WebView webView = null;
  private Sponsor_Entity sponsor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sponsor_details);
    sponsor = (Sponsor_Entity) (getIntent().getParcelableArrayListExtra("sponsor")).get(0);
    ((TextView) findViewById(R.id.tvTitle)).setText(sponsor.getName());
    webView = (WebView) findViewById(R.id.wv_internal);
    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");
    webView.getSettings().setAppCacheEnabled(true);
    webView.getSettings().setAllowFileAccess(true);
    webView.getSettings().setPluginState(PluginState.ON);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebChromeClient(new WebChromeClient());
    webView.getSettings().setLoadWithOverviewMode(true);
    webView.getSettings().setUseWideViewPort(true);
    if (sponsor.getDetails() != null && !sponsor.getDetails().trim().equals("")) {
      String summary = "<html><body>" + sponsor.getDetails() + "</body></html>";
      webView.loadDataWithBaseURL(null, summary, "text/html", "UTF-8", null);
      webView.setWebViewClient(
          new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
              } else if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
              }
              return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
              super.onPageFinished(view, url);
            }
          });
    }
  }
}
