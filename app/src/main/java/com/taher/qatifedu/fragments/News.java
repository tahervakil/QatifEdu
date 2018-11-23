package com.taher.qatifedu.fragments;

import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.entity.Section_Entity;
import com.taher.qatifedu.entity.TikerNews_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class News extends /*Sherlock*/ Fragment {
  View myFragmentView;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private com.taher.qatifedu.utility.ContentUpdater updater;
  private /*SherlockFragment*/ AppCompatActivity act;
  private GridView griddview;
  public static ArrayList<Section_Entity> alSectionsMainDataList;
  private ArrayList<Banner_Entity> alBannersMainDataList;
  private ArrayList<TikerNews_Entity> alTikerMainDataList;
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  static GridViewCustomAdapter adapter;
  private ProgressDialog progress = null;
  private LinearLayout ln_Banner;
  // private LinearLayout ln_Tiker;
  private String name;
  private int width = 0, height = 0, x, x2, total_positive, total_positive2;
  private Handler handler, handler2;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity) */ (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
      myFragmentView = inflater.inflate(R.layout.news, container, false);
      Display localDisplay = getActivity().getWindowManager().getDefaultDisplay();
      width = 100 * localDisplay.getWidth() / 100;
      height = 13 * localDisplay.getHeight() / 100;
      options =
          new DisplayImageOptions.Builder()
              .cacheInMemory()
              .cacheOnDisc()
              .bitmapConfig(Bitmap.Config.RGB_565)
              .build();
      alSectionsMainDataList = new ArrayList<Section_Entity>();
      alBannersMainDataList = new ArrayList<Banner_Entity>();
      alTikerMainDataList = new ArrayList<TikerNews_Entity>();
      ln_Banner = (LinearLayout) myFragmentView.findViewById(R.id.banner_layout);
      // ln_Tiker=(LinearLayout) myFragmentView.findViewById(R.id.tiker_layout);
      dbHelper = new DatabaseHelper(act);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(act, db);
      griddview = (GridView) myFragmentView.findViewById(R.id.gridView);
      adapter = new GridViewCustomAdapter(act);
      griddview.setAdapter(adapter);
      griddview.setOnItemClickListener(
          new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
              if (alSectionsMainDataList != null && alSectionsMainDataList.get(position) != null) {
                Section_Entity section = (Section_Entity) alSectionsMainDataList.get(position);
                Bundle data = new Bundle();
                data.putString(Constants.SECTIONID, section.getId());
                data.putString(Constants.NAME, section.getName());
                Fragment fragment = new News_List();
                fragment.setArguments(data);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (getFragmentManager().findFragmentByTag(name) == null) ;
                ft.addToBackStack(null);
                ft.replace(R.id.frame_container, Constants.NFragmentStack.push(fragment), name)
                    .commit();
              }
            }
          });
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);
      new FetchSections_NewsData().execute();
      /* new FetchBannersData().execute();
      new FetchTikerData().execute();*/
    } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  public void ScrollRight(final HorizontalScrollView v) {
    new CountDownTimer(2000, 20) {

      public void onTick(long millisUntilFinished) {
        if ((2000 - millisUntilFinished) == v.getRight()) millisUntilFinished = 2000;
        v.scrollTo(0, (int) (2000 - millisUntilFinished));
      }

      public void onFinish() {}
    }.start();
  }

  private Cursor getSectionsCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s from %s ",
              Constants.ID,
              Constants.TITLE,
              Constants.LOGO,
              Constants.SECTIONS_TABLE);
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

  private class FetchSections_NewsData extends AsyncTask<Void, Void, Void> {
    ArrayList<Section_Entity> alSectionsData = new ArrayList<Section_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          String strChangeDate = updater.getChangeDate(Constants.NEWS_TABLE);
          updater.ContentsForNews(strChangeDate);
          Cursor crsr = getSectionsCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              Section_Entity cursorEntity = new Section_Entity();
              cursorEntity.setId(crsr.getString(0));
              cursorEntity.setName(crsr.getString(1));
              cursorEntity.setLogo(crsr.getString(2));
              ;
              cursorEntity.setUnread(updater.NewsUnReadCount(cursorEntity.getId()));
              alSectionsData.add(cursorEntity);
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
      alSectionsMainDataList.clear();
      if (alSectionsData != null && alSectionsData.size() > 0) {
        alSectionsMainDataList = alSectionsData;
      } else if (alSectionsMainDataList.size() == 0) {
        Utility.showToast(act, getString(R.string.nodata_found));
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
        for (int i = 0; i < alSectionsMainDataList.size(); i++) {
          final Section_Entity section = alSectionsMainDataList.get(i);
          section.setUnread(updater.NewsUnReadCount(section.getId()));
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
                if (getFragmentManager().findFragmentByTag(name) == null) ;
                ft.addToBackStack(null);
                ft.replace(R.id.frame_container, Constants.NFragmentStack.push(fragment), name)
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
      // LoadTiker();
    }
  }

  //  public void LoadTiker() {
  //    for (int i = 0; i < alTikerMainDataList.size(); i++) {
  //
  //      TikerNews_Entity tiker = alTikerMainDataList.get(i);
  //      final TextView text = new TextView(getActivity());
  //      text.setText("         " + tiker.getTitle());
  //      text.setLines(1);
  //      text.setTextSize(18);
  //      text.setTextColor(Color.WHITE);
  //      text.setGravity(Gravity.CENTER);
  //      ln_Tiker.addView(text);
  //    }
  //
  //    total_positive2 = alTikerMainDataList.size() * width;
  //
  //    ln_Tiker.scrollBy(total_positive2, 0);
  //
  //    if (handler2 == null) {
  //
  //      handler2 = new Handler();
  //      handler2.postDelayed(runnable2, 0);
  //
  //    } else {
  //
  //      total_positive2 = alTikerMainDataList.size() * width;
  //    }
  //  }

  /*private Runnable runnable2 =
  new Runnable() {
    @Override
    public void run() {
      */
  /* do what you need to do */
  /*
  ln_Tiker.scrollBy(-2, 0);

  if (x2 >= (total_positive2 + width)) {
    ln_Tiker.scrollTo(total_positive2, 0);
    x2 = 0;

  } else {
    x2 = x2 + 2;
  }

  */
  /* and here comes the "trick" */
  /*
      handler2.postDelayed(this, 20);
    }
  };*/

  public class GridViewCustomAdapter extends ArrayAdapter<Object> {
    Context context;

    public GridViewCustomAdapter(Context context) {
      super(context, 0);
      this.context = context;
    }

    public int getCount() {
      return alSectionsMainDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (alSectionsMainDataList == null || alSectionsMainDataList.size() == 0) return convertView;

      LayoutInflater inf = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
      convertView = inf.inflate(R.layout.ads_grid_item, null);
      TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
      final TextView tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
      final ImageView iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
      final View pd = convertView.findViewById(R.id.screener_pd);

      final Section_Entity section = alSectionsMainDataList.get(position);

      if (section != null && section.getName() != null) tv_title.setText(section.getName());

      String strCount = Integer.toString(section.getUnread());
      if (strCount.equals("0")) tv_unread.setVisibility(View.GONE);
      else tv_unread.setText(Integer.toString(section.getUnread()));
      tv_unread.setOnClickListener(
          new OnClickListener() {

            @Override
            public void onClick(View v) {
              updater.updateNewsViewedStatus(section.getId(), 1);
              section.setUnread(0);
              tv_unread.setVisibility(View.GONE);
            }
          });
      try {
        if (section != null && section.getLogo() != null) {
          imageLoader.displayImage(
              act.getString(R.string.section_image_url) + section.getLogo(),
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

  @Override
  public void onResume() {
    ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
    act.getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.btn_refresh)
        .setVisibility(View.GONE);
    act.getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.extra_category)
        .setVisibility(View.GONE);
    act.getSupportActionBar().getCustomView().findViewById(R.id.calendar).setVisibility(View.GONE);
    new UpdateNumView().execute();
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }
}
