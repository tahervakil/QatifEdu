package com.taher.qatifedu.fragments;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
/*
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.FetchApiResponse;
import com.taher.qatifedu.utility.Utility;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.TextView;

public class Banner extends Fragment {
  WebView webView = null;
  View myFragmentView;
  View screener;
  private AppCompatActivity act;
  private ArrayList<Banner_Entity> alBannerMainData;
  private Button btn_Next, btn_Previous;
  int iPos;
  String name;
  private SQLiteDatabase db;
  private DatabaseHelper dbHelper;
  private ContentUpdater updater;
  private Banner_Entity banner;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
      myFragmentView = inflater.inflate(R.layout.banner, container, false);
      screener = myFragmentView.findViewById(R.id.screener_pd);
      btn_Previous = (Button) myFragmentView.findViewById(R.id.btn_previous);
      btn_Previous.setOnClickListener(
          new OnClickListener() {
            public void onClick(View v) {
              try {
                if (iPos != 0) {
                  iPos = iPos - 1;
                  setData(iPos);
                }
                CheckPosition(iPos);
              } catch (Exception ex) {
              }
            }
          });

      btn_Next = (Button) myFragmentView.findViewById(R.id.btn_next);
      btn_Next.setOnClickListener(
          new OnClickListener() {
            public void onClick(View v) {
              try {
                if (iPos != alBannerMainData.size() - 1) {
                  iPos = iPos + 1;
                  setData(iPos);
                }
                CheckPosition(iPos);
              } catch (Exception ex) {
              }
            }
          });
      Bundle bundle = this.getArguments();
      alBannerMainData = bundle.getParcelableArrayList("Banner");
      iPos = bundle.getInt(Constants.POS);
      ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(getString(R.string.details));

      screener.setVisibility(View.VISIBLE);

      dbHelper = new DatabaseHelper(act);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(act, db);
      setData(iPos);
    } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  @Override
  public void onPause() {
    super.onPause();

    try {
      Class.forName("android.webkit.WebView")
          .getMethod("onPause", (Class[]) null)
          .invoke(webView, (Object[]) null);

    } catch (ClassNotFoundException cnfe) {
    } catch (NoSuchMethodException nsme) {
    } catch (InvocationTargetException ite) {
    } catch (IllegalAccessException iae) {
    }
  }

  @Override
  public void onResume() {
    try {
      Class.forName("android.webkit.WebView")
          .getMethod("onResume", (Class[]) null)
          .invoke(webView, (Object[]) null);

    } catch (ClassNotFoundException cnfe) {
    } catch (NoSuchMethodException nsme) {
    } catch (InvocationTargetException ite) {
    } catch (IllegalAccessException iae) {
    }
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }

  public void CheckPosition(int position) {
    btn_Previous.setBackgroundResource(R.drawable.pervious);
    btn_Next.setBackgroundResource(R.drawable.next);
    if (iPos == 0) btn_Previous.setBackgroundResource(R.drawable.perviousdisabled);

    if (position == alBannerMainData.size() - 1)
      btn_Next.setBackgroundResource(R.drawable.nextdisabled);
  }

  public void setData(int iPos) {
    banner = alBannerMainData.get(iPos);
    webView = (WebView) myFragmentView.findViewById(R.id.wv_internal);
    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");
    webView.getSettings().setAppCacheEnabled(true);
    webView.getSettings().setAllowFileAccess(true);
    webView.getSettings().setPluginState(PluginState.ON);
    webView.getSettings().setJavaScriptEnabled(true);
    // webView.getSettings().setLoadWithOverviewMode(true);
    // webView.getSettings().setUseWideViewPort(true);
    webView.setWebChromeClient(new WebChromeClient());
    webView.setBackgroundColor(0x00000000);
    if (banner.getDetials() != null && !banner.getDetials().trim().equals("")) {
      String summary = "<html><body>" + banner.getDetials() + "</body></html>";
      webView.loadDataWithBaseURL(null, summary, "text/html", "UTF-8", null);
    }
    webView.setWebViewClient(
        new WebViewClient() {
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
          }

          @Override
          public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            screener.setVisibility(View.GONE);
          }
        });
    webView.setOnKeyListener(
        new OnKeyListener() {

          public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
              handler.sendEmptyMessage(1);
              return true;
            }
            return false;
          }
        });
    if (Utility.isConnected(act)) {
      new ViewCountLoader().execute(new Void[] {null});
    } else Utility.showToast(act, getString(R.string.alert_need_internet_connection));

    ((TextView) myFragmentView.findViewById(R.id.tv_title)).setText(banner.getTitle());
    CheckPosition(iPos);
  }

  private Handler handler =
      new Handler() {
        @Override
        public void handleMessage(Message message) {
          switch (message.what) {
            case 1:
              {
                webViewGoBack();
              }
              break;
          }
        }
      };

  private void webViewGoBack() {
    webView.goBack();
  }

  public class ViewCountLoader extends AsyncTask<Void, Void, String> {
    String noofViews = "";

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Void... arg0) {
      if (Utility.isConnected(act)) {
        noofViews =
            new FetchApiResponse()
                .getJsonValue(
                    "http://qatifedu.com/cmsQatifedu/Services/frmViews.aspx?id="
                        + banner.getID()
                        + "&type=3&addOne=1");
        updater.updateNumViewd(banner.getID(), "3", noofViews);
      } else {
        noofViews = updater.selectNumViews(banner.getID(), "3");
      }

      return noofViews;
    }

    @Override
    protected void onPostExecute(String v) {
      try {
        ((TextView) myFragmentView.findViewById(R.id.tv_views))
            .setText(getString(R.string.views) + " : " + v);
      } catch (Exception ex) {
      }
    }
  }
}
