package com.taher.qatifedu.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.CustomAdapter;
import com.taher.qatifedu.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/

public class AdsFavorite_List extends /*Sherlock*/ Fragment implements CustomAdapter.ListFiller {
  View myFragmentView;
  private ListView lvList;
  private CustomAdapter adapter;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private ContentUpdater updater;
  private /*SherlockFragment*/ AppCompatActivity act;
  private ArrayList<Ads_Entity> alAdsMainDataList;
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  private ProgressDialog progress = null;
  private String name = "ads_favorite";

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity) */ (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
      myFragmentView = inflater.inflate(R.layout.listing, container, false);
      options =
          new DisplayImageOptions.Builder()
              .cacheInMemory()
              .cacheOnDisc()
              .bitmapConfig(Bitmap.Config.RGB_565)
              .build();
      alAdsMainDataList = new ArrayList<Ads_Entity>();
      lvList = (ListView) myFragmentView.findViewById(R.id.lvList);
      adapter = new CustomAdapter(act, alAdsMainDataList, R.layout.listitem, this);
      lvList.setAdapter(adapter);
      lvList.setOnItemClickListener(
          new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
              if (alAdsMainDataList != null && alAdsMainDataList.get(pos) != null) {
                Bundle data = new Bundle();
                data.putString(Constants.NAME, alAdsMainDataList.get(pos).getTitle());
                data.putInt(Constants.POS, pos);
                data.putParcelableArrayList("Ads", alAdsMainDataList);
                Fragment fragment = new Ads_Details();
                fragment.setArguments(data);
                FragmentTransaction ft =
                    getActivity().getSupportFragmentManager().beginTransaction();
                if (getFragmentManager().findFragmentByTag(name) == null) ;
                ft.addToBackStack(null);
                ft.replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), name)
                    .commit();
              }
            }
          });

      lvList.setOnItemLongClickListener(
          new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
              if (alAdsMainDataList != null && alAdsMainDataList.get(0) != null) {
                try {
                  final Ads_Entity ads = alAdsMainDataList.get(pos);
                  AlertDialog.Builder bld = new AlertDialog.Builder(act);
                  bld.setMessage(getString(R.string.deletenews_msg));
                  bld.setNegativeButton(
                      getString(R.string.cancel),
                      new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                      });
                  bld.setPositiveButton(
                      getString(R.string.ok),
                      new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          updater.deleteFavoriteAds(ads.getId());
                          alAdsMainDataList.remove(ads);
                          adapter.notifyDataSetChanged();
                        }
                      });

                  bld.create().show();

                } catch (Exception activityException) {
                  Log.e("helloandroid dialing example", "Call failed", activityException);
                }
              }
              return false;
            }
          });
      dbHelper = new DatabaseHelper(act);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(act, db);
    } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  @Override
  public void onResume() {
    new FetchAdsData().execute();
    ((ImageButton) act.getSupportActionBar().getCustomView().findViewById(R.id.btn_refresh))
        .setVisibility(View.GONE);
    act.getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.extra_category)
        .setVisibility(View.GONE);
    act.getSupportActionBar().getCustomView().findViewById(R.id.calendar).setVisibility(View.GONE);
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }

  private Cursor getAdsContentsCursor() {
    Cursor crsr = null;
    try {
      String sql =
          String.format(
              Locale.US,
              "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s order by %s DESC ",
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
              Constants.ADS_FAVORITE_TABLE,
              Constants.SECTIONID,
              " LastChange ");
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
  }

  private class FetchAdsData extends AsyncTask<Void, Void, Void> {
    ArrayList<Ads_Entity> alAdsData = new ArrayList<Ads_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          Cursor cursor = getAdsContentsCursor();
          Log.e("Ads", "Count " + cursor.getCount());
          for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Ads_Entity cursorEntity = new Ads_Entity();
            cursorEntity.setId(cursor.getString(0));
            cursorEntity.setSectionID(cursor.getString(1));
            cursorEntity.setTitle(cursor.getString(2));
            cursorEntity.setText(cursor.getString(3));
            cursorEntity.setImage(cursor.getString(4));
            cursorEntity.setStartDate(cursor.getString(5));
            cursorEntity.setEndDate(cursor.getString(6));
            cursorEntity.setLastChange(cursor.getString(7));
            cursorEntity.setViewed(cursor.getInt(8));
            cursorEntity.setStatus(cursor.getInt(9));
            // cursorEntity.setIsFirst(cursor.getInt(10));
            alAdsData.add(cursorEntity);
          }
          cursor.close();

        } catch (Exception exception) {
          exception.printStackTrace();
          return null;
        }
      } catch (Exception exception) {
        exception.printStackTrace();
        return null;
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      super.onPostExecute(v);
      progress.dismiss();
      alAdsMainDataList.clear();
      if (alAdsData != null && alAdsData.size() > 0) {
        alAdsMainDataList = alAdsData;
      }

      adapter.notifyDataSetChanged();
    }
  }

  @Override
  public void fillListData(View v, int pos) {}

  @Override
  public boolean isEnabled(int pos) {
    return true;
  }

  @Override
  public boolean useExtGetView() {
    return true;
  }

  @Override
  public View getView(int pos, View contextView) {
    try {

      if (alAdsMainDataList == null || alAdsMainDataList.size() == 0) return contextView;

      LayoutInflater inf = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
      contextView = inf.inflate(R.layout.listitem, null);
      final ViewHolder holder = new ViewHolder();
      holder.tv_title = (TextView) contextView.findViewById(R.id.tv_title);
      holder.tv_date = (TextView) contextView.findViewById(R.id.tv_date);
      holder.iv_viewd = (ImageView) contextView.findViewById(R.id.iv_read);
      // holder.iv_status = (ImageView) contextView.findViewById(R.id.iv_status);
      holder.iv_image = (ImageView) contextView.findViewById(R.id.iv_image);
      holder.ln_container = contextView.findViewById(R.id.container);

      holder.iv_viewd.setVisibility(View.GONE);

      holder.pd = contextView.findViewById(R.id.screener_pd);
      final Ads_Entity Ads = (Ads_Entity) alAdsMainDataList.get(pos);

      if (Ads != null && Ads.getTitle() != null) holder.tv_title.setText(Ads.getTitle());

      if (Ads != null && Ads.getStartDate() != null) holder.tv_date.setText(Ads.getStartDate());

      if (Ads.getIsFirst() == 1) Ads.setStatus(17);
      if (Ads.getStatus() == 10 || Ads.getStatus() == 0) {
        if (Ads.getViewed() == 0) {
          // holder.iv_status.setVisibility(View.VISIBLE);
        } else {
          // holder.iv_status.setVisibility(View.INVISIBLE);
        }
      }

      // SetStatusImage(holder.iv_status, Ads.getStatus());

      if (Ads.getIsFirst() == 1)
        holder.ln_container.setBackgroundColor(getResources().getColor(R.color.yellow));
      else holder.ln_container.setBackgroundColor(getResources().getColor(R.color.white));

      if (Ads != null && Ads.getImage() != null) {
        imageLoader.displayImage(
            act.getString(R.string.ads_image_url) + Ads.getImage(),
            holder.iv_image,
            options,
            new SimpleImageLoadingListener() {
              @Override
              public void onLoadingStarted(String imageUri, View view) {
                holder.pd.setVisibility(View.VISIBLE);
              }

              @Override
              public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.pd.setVisibility(View.GONE);
                holder.iv_image.setImageDrawable(
                    getResources().getDrawable(R.drawable.defaultimage));
              }

              @Override
              public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.pd.setVisibility(View.GONE);
              }
            });
      } else holder.iv_image.setImageDrawable(getResources().getDrawable(R.drawable.defaultimage));

    } catch (Exception ex) {
      ex.getMessage();
    }
    return contextView;
  }

  class ViewHolder {
    private ImageView iv_viewd, iv_image;
    private TextView tv_title, tv_date;
    private View pd;
    private ConstraintLayout ln_container;
  }

  @Override
  public String getFilter() {
    return "";
  }

  @Override
  public ArrayList<Ads_Entity> getFilteredList() {
    return alAdsMainDataList;
  }

  public void SetStatusImage(ImageView iv_status, int status) {

    switch (status) {
      case 0:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.new_item));
        break;

      case 1:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.urgent));
        break;

      case 2:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.special_offers));
        break;

      case 3:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.new_offers));
        break;

      case 4:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.reduction));
        break;

      case 5:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.discounts));
        break;

      case 6:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.opening));
        break;

      case 7:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.limited_offers));
        break;

      case 8:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.very_important));
        break;

      case 9:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.reminder));
        break;

      case 10:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.new_item));
        break;

      case 11:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.terms));
        break;

      case 12:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.call));
        break;

      case 13:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.quik));
        break;

      case 14:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.offers));
        break;

      case 15:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.free));
        break;

      case 16:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.soon));
        break;
      case 17:
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.stick));
        break;

      default:
    }
  }
}
