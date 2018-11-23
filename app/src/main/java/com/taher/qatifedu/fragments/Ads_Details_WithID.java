package com.taher.qatifedu.fragments;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.FetchApiResponse;
import com.taher.qatifedu.utility.Utility;
import com.taher.qatifedu.utility.WebView_AdsDetails_WithID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Ads_Details_WithID extends /*Sherlock*/ Fragment {
  static View myFragmentView;
  private SQLiteDatabase db;
  private DatabaseHelper dbHelper;
  private static ContentUpdater updater;
  private static WebView_AdsDetails_WithID webView = null;
  private static Button btn_Favorite;
  private Button btn_Share;
  private Button btn_Next;
  private Button btn_Previous;
  private Ads_Entity Ads;
  private ArrayList<Ads_Entity> alAdsMainData;
  private ArrayList<Banner_Entity> alBannersMainDataList;
  private static /*SherlockFragment*/ AppCompatActivity act;
  String name, id;
  private LinearLayout ln_Banner;
  private int width = 0, height = 0, x, total_positive;
  private Handler handler;
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  static Ads_Details_WithID Ads_DetailsFrag;
  private ProgressDialog progress = null;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity) */ (AppCompatActivity) getActivity();
    Ads_DetailsFrag = this;
    // if (myFragmentView == null) {
    myFragmentView = inflater.inflate(R.layout.details_withid, container, false);
    webView = (WebView_AdsDetails_WithID) myFragmentView.findViewById(R.id.wv_internal);

    Display localDisplay = getActivity().getWindowManager().getDefaultDisplay();
    width = 100 * localDisplay.getWidth() / 100;
    height = 13 * localDisplay.getHeight() / 100;
    options =
        new DisplayImageOptions.Builder()
            .cacheInMemory()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    alAdsMainData = new ArrayList<Ads_Entity>();
    alBannersMainDataList = new ArrayList<Banner_Entity>();
    ln_Banner = (LinearLayout) myFragmentView.findViewById(R.id.banner_layout);
    btn_Favorite = (Button) myFragmentView.findViewById(R.id.btn_favorite);
    btn_Favorite.setOnClickListener(
        new OnClickListener() {
          public void onClick(View v) {
            try {
              Object obj[] = (Object[]) btn_Favorite.getTag();
              if (obj[0].toString().compareTo("Favorite") == 0) {
                updater.deleteFavoriteAds(Ads.getId(), Ads.getSectionID());
                btn_Favorite.setTag(new Object[] {"NotFavorite"});
                btn_Favorite.setBackgroundResource(R.drawable.not_favorite);
              } else {
                updater.saveContentsForFavorite_Ads(Ads);
                btn_Favorite.setTag(new Object[] {"Favorite"});
                btn_Favorite.setBackgroundResource(R.drawable.favorite);
              }
            } catch (Exception ex) {
            }
          }
        });

    btn_Previous = (Button) myFragmentView.findViewById(R.id.btn_previous);
    btn_Previous.setOnClickListener(
        new OnClickListener() {
          public void onClick(View v) {
            /*try{
            if(iPos!=0){
            	iPos=iPos-1;
            	setData(iPos);}
                CheckPosition(iPos);
                updateAdsViewedStatus(Ads.getId(),1);
            	Ads.setViewed(1);}
            	catch(Exception ex){}*/ }
        });

    btn_Next = (Button) myFragmentView.findViewById(R.id.btn_next);
    btn_Next.setOnClickListener(
        new OnClickListener() {
          public void onClick(View v) {
            /*try{
              	if(iPos!=alAdsMainData.size()-1){
              		iPos=iPos+1;
            	setData(iPos);}
                  CheckPosition(iPos);
                  updateAdsViewedStatus(Ads.getId(),1);
            Ads.setViewed(1);}
            catch(Exception ex){}*/ }
        });

    btn_Share = (Button) myFragmentView.findViewById(R.id.btn_share);
    btn_Share.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Ads.getTitle());
            sharingIntent.putExtra(
                android.content.Intent.EXTRA_TEXT,
                Ads.getTitle()
                    + "\n"
                    + "http://qatifedu.com/cmsQatifedu/frmAds.aspx?id="
                    + Ads.getId()
                    + "\n"
                    + "(تطبيق صفوى الاعلانية)");
            startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), 1);
          }
        });

    Bundle bundle = this.getArguments();
    // name=bundle.getString(Constants.NAME);
    id = Integer.toString(bundle.getInt(Constants.ID, 0));
    dbHelper = new DatabaseHelper(act);
    db = dbHelper.getWritableDatabase();
    updater = new ContentUpdater(act, db);
    // }
    // else
    // ((ViewGroup)myFragmentView.getParent()).removeView(myFragmentView);
    if (Utility.isConnected(act)) new Fetch_AdsData().execute(new String[] {id});
    else Utility.showToast(act, getString(R.string.alert_need_internet_connection));
    new FetchBannersData().execute();
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

  private Cursor getAdsContentsCursorByID(String ID) {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%s  "
                  + " AND Deleted='0' ",
              Constants.ID,
              Constants.SECTIONID,
              Constants.TITLE,
              Constants.TEXT,
              Constants.IMAGE,
              Constants.STARTDATE,
              Constants.ENDDATE,
              Constants.LASTCHANGE,
              Constants.VIEWD,
              Constants.STATUS,
              Constants.ISFIRST,
              Constants.ENDDATESTRING,
              Constants.ADS_TABLE,
              Constants.ID,
              ID);

      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
  }

  private class Fetch_AdsData extends AsyncTask<String, Void, Void> {
    ArrayList<Ads_Entity> alAdsData = new ArrayList<Ads_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(String... params) {
      try {
        if (Utility.isConnected(act)) {
          updater.ContentsForAdsByID(params[0]);
        }
        Cursor crsr = getAdsContentsCursorByID(params[0]);
        crsr.moveToFirst();
        if (crsr != null) {
          while (!crsr.isAfterLast()) {
            Ads_Entity cursorEntity = new Ads_Entity();
            cursorEntity.setId(crsr.getString(0));
            cursorEntity.setSectionID(crsr.getString(1));
            cursorEntity.setTitle(crsr.getString(2));
            cursorEntity.setText(crsr.getString(3));
            cursorEntity.setImage(crsr.getString(4));
            cursorEntity.setStartDate(crsr.getString(5));
            cursorEntity.setEndDate(crsr.getString(6));
            cursorEntity.setLastChange(crsr.getString(7));
            cursorEntity.setViewed(crsr.getInt(8));
            cursorEntity.setStatus(crsr.getInt(9));
            cursorEntity.setIsFirst(crsr.getInt(10));
            if (Utility.isExpired(crsr.getString(11)) == false) alAdsData.add(cursorEntity);
            crsr.moveToNext();
          }
          crsr.close();
        }

      } catch (Exception exception) {
        return null;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      super.onPostExecute(v);
      progress.dismiss();
      alAdsMainData.clear();
      if (alAdsData != null && alAdsData.size() > 0) {
        alAdsMainData = alAdsData;
        setData(0);
      } else if (alAdsMainData.size() == 0) {
        Utility.showToast(act, getString(R.string.nodata_found));
      }
    }
  }

  private boolean updateAdsViewedStatus(String id, int Adstatus) {
    boolean result = false;
    try {
      String sql =
          String.format(
              Locale.US,
              "UPDATE %s SET %s = %s WHERE (%s = %s)",
              Constants.ADS_TABLE,
              Constants.VIEWD,
              Adstatus,
              Constants.ID,
              id);
      db.execSQL(sql);
      result = true;
    } catch (Exception ex) {
      ex.getMessage();
    }
    return result;
  }
  /*
   public static void CheckPosition(int position){
   btn_Previous.setBackgroundResource(R.drawable.pervious);
   btn_Next.setBackgroundResource(R.drawable.next);
    if(iPos==0)
  	  btn_Previous.setBackgroundResource(R.drawable.perviousdisabled);
      	if(position== alAdsMainData.size()-1)
      		btn_Next.setBackgroundResource(R.drawable.nextdisabled);
  }*/

  public void IsFavorite() {
    int iIsFavorite = updater.IsFavorite_Ads(Ads.getId());
    if (iIsFavorite > 0) {
      btn_Favorite.setTag(new Object[] {"Favorite"});
      btn_Favorite.setBackgroundResource(R.drawable.favorite);
    } else {
      btn_Favorite.setTag(new Object[] {"NotFavorite"});
      btn_Favorite.setBackgroundResource(R.drawable.not_favorite);
    }
  }

  private Cursor getBannersCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s,%s from %s order by %s DESC ",
              Constants.ID,
              Constants.TITLE,
              Constants.DETIALS,
              Constants.IMAGE,
              Constants.BANNER_TABLE,
              " LastChange ");
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
  }

  private class FetchBannersData extends AsyncTask<Void, Void, Void> {
    ArrayList<Banner_Entity> alBannersData = new ArrayList<Banner_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          Cursor crsr = getBannersCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              Banner_Entity cursorEntity = new Banner_Entity();
              cursorEntity.setID(crsr.getString(0));
              cursorEntity.setTitle(crsr.getString(1));
              cursorEntity.setDetials(crsr.getString(2));
              cursorEntity.setImage(crsr.getString(3));
              alBannersData.add(cursorEntity);
              crsr.moveToNext();
            }
            crsr.close();
          }
        } catch (Exception exception) {
          return null;
        }
      } catch (Exception exception) {
        return null;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      super.onPostExecute(v);
      alBannersMainDataList.clear();
      if (alBannersData != null && alBannersData.size() > 0) {
        alBannersMainDataList = alBannersData;
      }
      if (isAdded()) {
        LoadBanners();
      }
    }
  }

  public void LoadBanners() {
    try {
      for (int i = 0; i < alBannersMainDataList.size(); i++) {
        final int pos = i;
        Banner_Entity banner = alBannersMainDataList.get(i);
        final ImageView image = new ImageView(getActivity());
        LayoutParams param = new LayoutParams(width, height);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setLayoutParams(param);
        image.setTag(i);

        image.setOnClickListener(
            new OnClickListener() {

              @Override
              public void onClick(View v) {
                if (alBannersMainDataList != null && alBannersMainDataList.get(pos) != null) {
                  Bundle data = new Bundle();
                  data.putString(Constants.NAME, alBannersMainDataList.get(pos).getTitle());
                  data.putInt(Constants.POS, pos);
                  data.putParcelableArrayList("Banner", alBannersMainDataList);
                  Fragment fragment = new Banner();
                  fragment.setArguments(data);
                  FragmentTransaction ft = getFragmentManager().beginTransaction();
                  if (getFragmentManager().findFragmentByTag("banner") == null) ;
                  ft.addToBackStack(null);
                  ft.replace(
                          R.id.frame_container, Constants.MFragmentStack.push(fragment), "banner")
                      .commit();
                }
              }
            });

        if (banner != null && banner.getImage() != null) {
          imageLoader.displayImage(
              "http://qatifedu.com/cmsQatifedu/Upload/Banner/" + banner.getImage(),
              image,
              options,
              new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                  image.setImageDrawable(getResources().getDrawable(R.drawable.banner_image));
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                  image.setImageDrawable(getResources().getDrawable(R.drawable.banner_image));
                }
              });
        }

        ln_Banner.addView(image);
      }

      total_positive = alBannersMainDataList.size() * width;

      ln_Banner.scrollBy(total_positive, 0);

      if (handler == null) {

        handler = new Handler();
        handler.postDelayed(runnable, 0);

      } else {

        total_positive = alBannersMainDataList.size() * width;
      }
    } catch (Exception ex) {
    }
  }

  private Runnable runnable =
      new Runnable() {
        @Override
        public void run() {
          /* do what you need to do */
          ln_Banner.scrollBy(-2, 0);

          if (x >= (total_positive + width)) {
            ln_Banner.scrollTo(total_positive, 0);
            x = 0;

          } else {
            x = x + 2;
          }

          /* and here comes the "trick" */
          handler.postDelayed(this, 10);
        }
      };

  public void setData(int iPos) {

    Ads = alAdsMainData.get(iPos);
    name = Ads.getTitle();
    ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
        .setText(getString(R.string.details));
    Ads_List.Pos = iPos;
    updateAdsViewedStatus(Ads.getId(), 1);
    // Ads_List.lvList.setSelection(iPos);
    // Ads_List.alAdsMainDataList.remove(iPos);
    // Ads_List.alAdsMainDataList.add(0,Ads);
    webView = (WebView_AdsDetails_WithID) myFragmentView.findViewById(R.id.wv_internal);
    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");
    webView.getSettings().setAppCacheEnabled(true);
    webView.getSettings().setAllowFileAccess(true);
    webView.getSettings().setPluginState(PluginState.ON);
    webView.getSettings().setJavaScriptEnabled(true);
    // webView.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));

    // webView.getSettings().setLoadWithOverviewMode(true);
    // webView.getSettings().setUseWideViewPort(true);
    webView.setFragment(Ads_DetailsFrag);
    webView.setWebChromeClient(new WebChromeClient());
    webView.setBackgroundColor(0x00000000);
    if (Ads.getText() != null && !Ads.getText().trim().equals("")) {
      String summary = "<html><body>" + Ads.getText() + "</body></html>";
      webView.loadDataWithBaseURL(null, summary, "text/html", "UTF-8", null);
      webView.setWebViewClient(
          new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                act.startActivity(intent);
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
    if (Utility.isConnected(act)) {
      new ViewCountLoader().execute(new Void[] {null});
    } else Utility.showToast(act, act.getString(R.string.alert_need_internet_connection));
    ((TextView) myFragmentView.findViewById(R.id.tv_title)).setText(Ads.getTitle());
    ((TextView) myFragmentView.findViewById(R.id.tv_date))
        .setText(act.getString(R.string.date) + " :" + Ads.getStartDate());
    // CheckPosition(iPos);
    IsFavorite();
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
                        + Ads.getId()
                        + "&type=2&addOne=1");
        updater.updateNumViewd(Ads.getId(), "2", noofViews);
      } else {
        noofViews = updater.selectNumViews(Ads.getId(), "2");
      }

      return noofViews;
    }

    @Override
    protected void onPostExecute(String v) {
      try {
        if (Ads_DetailsFrag.isAdded()) {}
        ((TextView) myFragmentView.findViewById(R.id.tv_views))
            .setText(act.getString(R.string.views) + " : " + v);
      } catch (Exception ex) {
      }
    }
  }

  @Override
  public void onResume() {
    act.getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.extra_category)
        .setVisibility(View.GONE);
    act.getSupportActionBar().getCustomView().findViewById(R.id.calendar).setVisibility(View.GONE);
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

  public void onSwipeRight() {
    try {
    } catch (Exception ex) {
    }
  }

  public void onSwipeLeft() {
    try {
    } catch (Exception ex) {
    }
  }
}
