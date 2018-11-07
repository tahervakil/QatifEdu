package com.taher.qatifedu.fragments;

import java.util.ArrayList;
import java.util.Locale;
import com.taher.qatifedu.R;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Ads extends Fragment {
  View myFragmentView;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private com.taher.qatifedu.utility.ContentUpdater updater;
  private AppCompatActivity actionBar;
  private GridView gridView;
  public static ArrayList<Company_Entity> alCompaniesMainDataList;
  private ArrayList<Banner_Entity> alBannersMainDataList;
  private ArrayList<TikerNews_Entity> alTikerMainDataList;

  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  public static GridViewCustomAdapter adapter;
  private ProgressDialog progress = null;
  private String name;
  private LinearLayout ln_Banner, ln_Tiker;
  private int width = 0, height = 0, x, x2, total_positive, total_positive2;
  private Handler handler, handler2;
  HorizontalScrollView layout;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    actionBar = (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
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
      alCompaniesMainDataList = new ArrayList<Company_Entity>();
      alBannersMainDataList = new ArrayList<Banner_Entity>();
      alTikerMainDataList = new ArrayList<TikerNews_Entity>();
      ln_Banner = (LinearLayout) myFragmentView.findViewById(R.id.banner_layout);
      ln_Tiker = (LinearLayout) myFragmentView.findViewById(R.id.tiker_layout);

      dbHelper = new DatabaseHelper(actionBar);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(actionBar, db);
      gridView = (GridView) myFragmentView.findViewById(R.id.gridView);
      adapter = new GridViewCustomAdapter(actionBar);
      gridView.setAdapter(adapter);
      gridView.setOnItemClickListener(
          new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
              if (alCompaniesMainDataList != null
                  && alCompaniesMainDataList.get(position) != null) {
                Company_Entity company = (Company_Entity) alCompaniesMainDataList.get(position);
                Bundle data = new Bundle();
                data.putString(Constants.SECTIONID, company.getId());
                data.putString(Constants.NAME, company.getName());
                Fragment fragment = new Ads_List();
                fragment.setArguments(data);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (getFragmentManager().findFragmentByTag(name) == null) ;
                ft.addToBackStack(null);
                ft.replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), name)
                    .commit();
              }
            }
          });
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      ((TextView) actionBar.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);
      new FetchCompanies_AdsData().execute();
      new FetchTikerData().execute();
      if (bundle.getInt(Constants.ID, 0) != (0)) {
        Bundle data = new Bundle();
        Fragment fragment = new Ads_Details_WithID();
        data = new Bundle();
        data.putInt(Constants.ID, bundle.getInt(Constants.ID, 0));
        fragment.setArguments(data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (getFragmentManager().findFragmentByTag(name) == null) ;
        ft.addToBackStack(null);
        ft.replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), name).commit();
      }
    } else ((ViewGroup) myFragmentView.getParent()).removeView(myFragmentView);

    return myFragmentView;
  }

  private Cursor getCompaniesCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s from %s  ",
              Constants.ID,
              Constants.TITLE,
              Constants.LOGO,
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

  private class FetchCompanies_AdsData extends AsyncTask<Void, Void, Void> {
    ArrayList<Company_Entity> alCompaniesData = new ArrayList<Company_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress =
          ProgressDialog.show(actionBar, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          if (Utility.isConnected(actionBar)) {
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
      super.onPostExecute(v);
      progress.dismiss();

      alCompaniesMainDataList.clear();
      if (alCompaniesData != null && alCompaniesData.size() > 0) {
        alCompaniesMainDataList = alCompaniesData;
      } else if (alCompaniesMainDataList.size() == 0) {
        Utility.showToast(actionBar, getString(R.string.nodata_found));
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
                image.setImageDrawable(getResources().getDrawable(R.drawable.icon_safwa));
              }

              @Override
              public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.icon_safwa));
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

  private class FetchTikerData extends AsyncTask<Void, Void, Void> {
    ArrayList<TikerNews_Entity> alTikerData = new ArrayList<TikerNews_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
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
      LoadTiker();
    }
  }

  public void LoadTiker() {
    for (int i = 0; i < alTikerMainDataList.size(); i++) {

      TikerNews_Entity tiker = alTikerMainDataList.get(i);
      final TextView text = new TextView(getActivity());
      text.setText("         " + tiker.getTitle());
      text.setLines(1);
      text.setTextSize(18);
      text.setTextColor(Color.BLACK);
      text.setGravity(Gravity.CENTER);
      ln_Tiker.addView(text);
    }

    total_positive2 = alTikerMainDataList.size() * width;

    ln_Tiker.scrollBy(total_positive2, 0);

    if (handler2 == null) {

      handler2 = new Handler();
      handler2.postDelayed(runnable2, 0);

    } else {

      total_positive2 = alTikerMainDataList.size() * width;
    }
  }

  private Runnable runnable2 =
      new Runnable() {
        @Override
        public void run() {
          /* do what you need to do */
          ln_Tiker.scrollBy(-2, 0);

          if (x2 >= (total_positive2 + width)) {
            ln_Tiker.scrollTo(total_positive2, 0);
            x2 = 0;

          } else {
            x2 = x2 + 2;
          }

          /* and here comes the "trick" */
          handler2.postDelayed(this, 20);
        }
      };

  public class GridViewCustomAdapter extends ArrayAdapter<Object> {
    Context context;

    public GridViewCustomAdapter(Context context) {
      super(context, 0);
      this.context = context;
    }

    public int getCount() {
      return alCompaniesMainDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (alCompaniesMainDataList == null || alCompaniesMainDataList.size() == 0)
        return convertView;

      LayoutInflater inf =
          (LayoutInflater) actionBar.getSystemService(actionBar.LAYOUT_INFLATER_SERVICE);
      convertView = inf.inflate(R.layout.ads_grid_item, null);
      TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
      final TextView tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
      final ImageView iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
      final View pd = convertView.findViewById(R.id.screener_pd);

      final Company_Entity company = alCompaniesMainDataList.get(position);

      if (company != null && company.getName() != null) tv_title.setText(company.getName());

      String strCount = Integer.toString(company.getUnread());

      if (strCount.equals("0")) {
        tv_unread.setVisibility(View.GONE);
      } else {
        tv_unread.setText(Integer.toString(company.getUnread()));
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
              actionBar.getString(R.string.companies_image_url) + company.getLogo(),
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
                  iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_safwa));
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                  pd.setVisibility(View.GONE);
                }
              });
        } else iv_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_safwa));

      } catch (Exception e) {
        e.getMessage();
      }

      return convertView;
    }
  }

  @Override
  public void onResume() {
    ((TextView) actionBar.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
        .setText(name);
    ((ImageButton) actionBar.getSupportActionBar().getCustomView().findViewById(R.id.btn_refreash))
        .setVisibility(View.GONE);
    new UpdateNumView().execute();
    super.onResume();
  }
}
