package com.taher.qatifedu.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.MainActivity;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Event_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.entity.Company_Entity;
import com.taher.qatifedu.entity.TikerNews_Entity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.taher.qatifedu.MainActivity.alCompaniesMainDataList;
import static com.taher.qatifedu.utility.Constants.TAG;

public class Ads extends Fragment {
  View myFragmentView;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private ContentUpdater updater;
  private AppCompatActivity appCompatActivity;
  private GridView gridView;
  public ArrayList<Company_Entity> companyMainCategory;
  private ArrayList<Banner_Entity> alBannersMainDataList;
  private ArrayList<TikerNews_Entity> alTikerMainDataList;
  private ArrayList<Event_Entity> eventEntityMainData;

  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  public static GridViewCustomAdapter adapter;
  private ProgressDialog progress = null;
  private String name;
  private LinearLayout ln_Banner;
  // private LinearLayout ln_Tiker;
  private int width = 0, height = 0, x, x2, total_positive, total_positive2;
  private Handler handler;
  HorizontalScrollView layout;
  ImageButton calendarButton;
  ImageButton eventButton;
  private Context context;

  SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
  SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:SS");

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  public void adjustFontScale(Configuration configuration) {

    DisplayMetrics metrics = getResources().getDisplayMetrics();

    if (configuration.fontScale > 1 || configuration.fontScale < 1) {
      configuration.fontScale = (float) 1;
    }

    metrics.scaledDensity = configuration.fontScale * metrics.density;
    getActivity().getBaseContext().getResources().updateConfiguration(configuration, metrics);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    appCompatActivity = (AppCompatActivity) getActivity();
    // adjustFontScale(getActivity().getResources().getConfiguration());

    if (myFragmentView == null) {
      Log.i("ADS", "AM I GETTING CALLED ON CREATE VIEW");

      myFragmentView = inflater.inflate(R.layout.news, container, false);
      Display localDisplay = getActivity().getWindowManager().getDefaultDisplay();
      width = 100 * localDisplay.getWidth() / 100;
      height = 17 * localDisplay.getHeight() / 100;
      layout = myFragmentView.findViewById(R.id.scroll_layout);

      ViewTreeObserver vto = layout.getViewTreeObserver();
      vto.addOnGlobalLayoutListener(
          new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
              height = layout.getMeasuredHeight();
              new FetchBannersData().execute();
            }
          });

      options =
          new DisplayImageOptions.Builder()
              .cacheInMemory()
              .cacheOnDisc()
              .bitmapConfig(Bitmap.Config.RGB_565)
              .build();

      // alCompaniesMainDataList = new ArrayList<>();
      companyMainCategory = new ArrayList<>();

      alBannersMainDataList = new ArrayList<>();
      alTikerMainDataList = new ArrayList<>();
      eventEntityMainData = new ArrayList<>();
      ln_Banner = myFragmentView.findViewById(R.id.banner_layout);

      dbHelper = new DatabaseHelper(getContext());
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(getContext(), db);
      gridView = myFragmentView.findViewById(R.id.gridView);

      adapter = new GridViewCustomAdapter(getActivity());

      gridView.setAdapter(adapter);
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      ((TextView)
              appCompatActivity.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);

      calendarButton =
          appCompatActivity.getSupportActionBar().getCustomView().findViewById(R.id.calendar);

      eventButton =
          appCompatActivity.getSupportActionBar().getCustomView().findViewById(R.id.extra_category);

      new FetchCompanies_AdsData().execute();
      new FetchEvent_Data().execute();

      calendarButton.setOnClickListener(
          new OnClickListener() {
            @Override
            public void onClick(View v) {

              String currentDate = dateFormatter.format(new Date());
              String currentTime = timeFormatter.format(new Date());
              new AlertDialog.Builder(context)
                  .setTitle("Date & Time")
                  .setMessage("Date: " + currentDate + "\nTime: " + currentTime)
                  .setNeutralButton(
                      "Done",
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                      })
                  .show();
            }
          });

      gridView.setOnItemClickListener(
          new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
              if (companyMainCategory != null && companyMainCategory.get(position) != null) {
                Company_Entity company = companyMainCategory.get(position);
                Bundle data = new Bundle();

                Log.i("ADS", "This is the unread value of Company -->" + company.getUnread());

                if (company.getSubCategoryType() == 0) {
                  data.putString(Constants.SECTIONID, company.getId());
                  data.putString(Constants.NAME, company.getName());
                  Fragment fragment = new Ads_List();
                  fragment.setArguments(data);
                  FragmentTransaction ft = getFragmentManager().beginTransaction();
                  if (getFragmentManager().findFragmentByTag(name) == null) ;
                  ft.addToBackStack(null);
                  ft.replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), name)
                      .commit();
                } else if (company.getSubCategoryType() == 1) {
                  Fragment fragment = new Ads();
                  data.putInt(Constants.ID, Integer.parseInt(company.getId()));
                  data.putString(Constants.NAME, company.getName());
                  fragment.setArguments(data);
                  FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                  if (getFragmentManager().findFragmentByTag(name) == null) ;
                  fragmentTransaction.addToBackStack(null);
                  fragmentTransaction.replace(R.id.frame_container, fragment).commit();
                }
              }
            }
          });

    } else {
      ((ViewGroup) myFragmentView).removeView(myFragmentView);
    }

    return myFragmentView;
  }

  @Override
  public void onResume() {

    Log.i("ADS", "AM I GETTING CALLED ON RESUME");

    ((TextView) appCompatActivity.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
        .setText(name);
    appCompatActivity
        .getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.extra_category)
        .setVisibility(View.VISIBLE);
    appCompatActivity
        .getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.calendar)
        .setVisibility(View.VISIBLE);

    appCompatActivity
        .getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.btn_refresh)
        .setVisibility(View.GONE);

    // new FetchCompanies_AdsData().execute();
    new UpdateNumView().execute();
    super.onResume();

    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }

  private Cursor getCompaniesCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s,%s,%s from %s  ",
              Constants.ID,
              Constants.TITLE,
              Constants.LOGO,
              Constants.PARENTID,
              Constants.SUB_CATEGORY_TYPE,
              Constants.COMPANIES_TABLE);
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
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

  private Cursor getTikerCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s from %s order by %s DESC ",
              Constants.ID,
              Constants.TITLE,
              Constants.TIKERNEWS_TABLE,
              " LastChange ");
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
  }

  private Cursor getEventCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s,%s,%s from %s",
              Constants.ID,
              Constants.TITLE,
              Constants.DETAILS,
              Constants.LASTCHANGE,
              Constants.LASTCHANGETYPE,
              Constants.EVENT_TABLE);
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      Log.e(TAG, "Exception thrown while getting event cursor " + ex.getMessage());
    }
    return crsr;
  }

  private class FetchCompanies_AdsData extends AsyncTask<Void, Void, Void> {
    ArrayList<Company_Entity> alCompaniesData = new ArrayList<Company_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress =
          ProgressDialog.show(
              appCompatActivity, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          if (Utility.isConnected(appCompatActivity)) {
            String strChangeDate = updater.getChangeDate(Constants.ADS_TABLE);
            updater.ContentsForAds(strChangeDate);
          }
          Cursor crsr = getCompaniesCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              Company_Entity cursorEntity = new Company_Entity();
              cursorEntity.setId(crsr.getString(0));
              cursorEntity.setName(crsr.getString(1));
              cursorEntity.setLogo(crsr.getString(2));
              cursorEntity.setParentId(crsr.getInt(3));
              cursorEntity.setSubCategoryType(crsr.getInt(4));
              cursorEntity.setUnread(updater.AdsUnReadCount(cursorEntity.getId()));
              alCompaniesData.add(cursorEntity);
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

      Log.i("ADS", "PROGRESS DIALOG IS SHOWING " + progress.isShowing());

      progress.dismiss();
      super.onPostExecute(v);

      companyMainCategory.clear();
      if (alCompaniesData != null && alCompaniesData.size() > 0) {
        // alCompaniesMainDataList.clear();
        alCompaniesMainDataList = alCompaniesData;
      } else if (alCompaniesMainDataList.size() == 0) {
        Utility.showToast(appCompatActivity, getString(R.string.nodata_found));
      }

      for (Company_Entity entity : alCompaniesMainDataList) {
        if (entity.getParentId() == 0 && getArguments().getInt(Constants.ID) == 0) {
          companyMainCategory.add(entity);
        } else if (entity.getParentId() != 0 && getArguments().getInt(Constants.ID) != 0) {
          if (getArguments().getInt(Constants.ID) == entity.getParentId()) {
            companyMainCategory.add(entity);
          }
        }
      }

      adapter.notifyDataSetChanged();
    }
  }

  private class UpdateNumView extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        for (int i = 0; i < alCompaniesMainDataList.size(); i++) {
          final Company_Entity company = alCompaniesMainDataList.get(i);
          company.setUnread(updater.AdsUnReadCount(company.getId()));
          Log.i("ADS", "Am I getting executed " + company.getName());
        }

      } catch (Exception exception) {
        return null;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      super.onPostExecute(v);
      adapter.notifyDataSetChanged();
    }
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
      progress.dismiss();
      alBannersMainDataList.clear();
      if (alBannersData != null && alBannersData.size() > 0) {
        alBannersMainDataList = alBannersData;
      }
      LoadBanners();
    }
  }

  public void LoadBanners() {

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
                ft.replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), "banner")
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
      } else {
        imageLoader.displayImage("drawable://" + R.drawable.banner_image, image);
      }
      ln_Banner.addView(image);
    }

    if (alBannersMainDataList.size() == 0) {
      final ImageView image = new ImageView(getActivity());

      LayoutParams param = new LayoutParams(width, height);
      image.setScaleType(ImageView.ScaleType.FIT_XY);
      image.setLayoutParams(param);
      image.setTag(0);
      imageLoader.displayImage("drawable://" + R.drawable.banner_image, image);

      // image.setImageDrawable(getResources().getDrawable(R.drawable.banner_image));
      ln_Banner.addView(image);
      total_positive = width;
    } else {
      total_positive = alBannersMainDataList.size() * width;
    }

    ln_Banner.scrollBy(total_positive, 0);

    if (handler == null) {

      handler = new Handler();
      handler.postDelayed(runnable, 0);

    } else {
      if (alBannersMainDataList.size() == 0) {
        total_positive = width;
      } else {
        total_positive = alBannersMainDataList.size() * width;
      }
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

  private class FetchEvent_Data extends AsyncTask<Void, Void, Void> {

    ArrayList<Event_Entity> event_entities = new ArrayList<>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

      if (event_entities != null) {
        eventEntityMainData = event_entities;
      }
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        try {

          if (Utility.isConnected(appCompatActivity)) {
            String strChangeDate = updater.getChangeDate(Constants.EVENT_TABLE);
            updater.ContentsForEvent(strChangeDate);
          }

          Cursor crsr = getEventCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              Event_Entity cursorEntity = new Event_Entity();
              cursorEntity.setId(crsr.getString(0));
              cursorEntity.setTitle(crsr.getString(1));
              cursorEntity.setDetails(crsr.getString(2));
              cursorEntity.setLastChange(crsr.getString(3));
              cursorEntity.setLastChangeType(crsr.getString(4));
              event_entities.add(cursorEntity);
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
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      progress.dismiss();
      if (event_entities != null) {
        eventEntityMainData = event_entities;
        Log.i(TAG, "COME HERE " + eventEntityMainData.get(0).getTitle());
      }
    }
  }

  private class FetchTikerData extends AsyncTask<Void, Void, Void> {
    ArrayList<TikerNews_Entity> alTikerData = new ArrayList<TikerNews_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      alTikerMainDataList.clear();
      if (alTikerData != null && alTikerData.size() > 0) {
        alTikerMainDataList = alTikerData;
      }
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          Cursor crsr = getTikerCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              TikerNews_Entity cursorEntity = new TikerNews_Entity();
              cursorEntity.setID(crsr.getString(0));
              cursorEntity.setTitle(crsr.getString(1));
              alTikerData.add(cursorEntity);
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
      progress.dismiss();
      alTikerMainDataList.clear();
      if (alTikerData != null && alTikerData.size() > 0) {
        alTikerMainDataList = alTikerData;
      }
    }
  }

  public class GridViewCustomAdapter extends ArrayAdapter<Object> {
    Context context;

    public GridViewCustomAdapter(Context context) {
      super(context, 0);
      this.context = context;
    }

    public int getCount() {
      return companyMainCategory.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (companyMainCategory == null || companyMainCategory.size() == 0) return convertView;

      LayoutInflater inf =
          (LayoutInflater)
              appCompatActivity.getSystemService(appCompatActivity.LAYOUT_INFLATER_SERVICE);
      convertView = inf.inflate(R.layout.ads_grid_item, null);

      int ROW_NUMBER = 4;

      // int cellHeight = StrictMath.max(parent.getHeight() / ROW_NUMBER -
      // parent.getContext().getResources().getDimensionPixelOffset(R.dimen.bootstrap_badge_default_size), 320);
      AbsListView.LayoutParams param =
          new AbsListView.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() / ROW_NUMBER);
      convertView.setLayoutParams(param);

      TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
      final TextView tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
      final ImageView iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
      final View pd = convertView.findViewById(R.id.screener_pd);

      final Company_Entity company = companyMainCategory.get(position);

      if (company != null && company.getName() != null) {
        tv_title.setText(company.getName());
      }

      int unreadCount = 0;

      if (company.getParentId() == 0 && company.getSubCategoryType() == 0) {
        for (Company_Entity company_entity : alCompaniesMainDataList) {

          if (company_entity.getId().equalsIgnoreCase(company.getId())) {
            unreadCount += company_entity.getUnread();
          }
        }
      } else {
        for (Company_Entity company_entity : alCompaniesMainDataList) {

          if (company_entity.getParentId() == Integer.parseInt(company.getId())) {
            unreadCount += company_entity.getUnread();
          }
        }

        if (company.getParentId() != 0 && company.getSubCategoryType() == 0) {

          unreadCount += company.getUnread();
        }
      }

      if (unreadCount == 0) {
        tv_unread.setVisibility(View.GONE);
      } else {
        tv_unread.setText(unreadCount + "");
      }

      tv_unread.setOnClickListener(
          new OnClickListener() {

            @Override
            public void onClick(View v) {
              updater.updateAdsViewedStatus(company.getId(), 1);
              company.setUnread(0);
              tv_unread.setVisibility(View.GONE);
            }
          });

      try {
        if (company != null && company.getLogo() != null) {
          imageLoader.displayImage(
              appCompatActivity.getString(R.string.companies_image_url) + company.getLogo(),
              iv_image,
              options,
              new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                  pd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                  pd.setVisibility(View.GONE);
                  iv_image.setImageDrawable(getResources().getDrawable(R.drawable.defaultimage));
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                  pd.setVisibility(View.GONE);
                }
              });
        } else iv_image.setImageDrawable(getResources().getDrawable(R.drawable.defaultimage));

      } catch (Exception e) {
        e.getMessage();
      }

      return convertView;
    }
  }
}
