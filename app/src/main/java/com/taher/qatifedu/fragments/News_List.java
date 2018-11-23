package com.taher.qatifedu.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.taher.qatifedu.entity.News_Entity;
import com.taher.qatifedu.entity.Section_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.CustomAdapter;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.Utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class News_List extends /*Sherlock*/ Fragment implements CustomAdapter.ListFiller {
  View myFragmentView;
  public static ListView lvList;
  private CustomAdapter adapter;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private ContentUpdater updater;
  private /*SherlockFragment*/ AppCompatActivity act;
  private ArrayList<News_Entity> alNewMainDataList;
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  private ProgressDialog progress = null;
  private String name, section_id;
  private ImageButton btnRefresh;
  public static int Pos;

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
      btnRefresh =
          (ImageButton) act.getSupportActionBar().getCustomView().findViewById(R.id.btn_refresh);
      btnRefresh.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (Utility.isConnected(getActivity())) new FetchSections_NewsData().execute();
              else
                Utility.showToast(
                    getActivity(), getString(R.string.alert_need_internet_connection));
            }
          });
      alNewMainDataList = new ArrayList<News_Entity>();
      lvList = (ListView) myFragmentView.findViewById(R.id.lvList);
      adapter = new CustomAdapter(act, alNewMainDataList, R.layout.listitem, this);
      lvList.setAdapter(adapter);
      lvList.setOnItemClickListener(
          new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
              if (alNewMainDataList != null && alNewMainDataList.get(pos) != null) {
                Bundle data = new Bundle();
                data.putString(Constants.NAME, alNewMainDataList.get(pos).getTitle());
                data.putInt(Constants.POS, pos);
                data.putParcelableArrayList("News", alNewMainDataList);
                Fragment fragment = new New_Details();
                fragment.setArguments(data);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (getFragmentManager().findFragmentByTag(name) == null) ;
                ft.addToBackStack(null);
                ft.replace(R.id.frame_container, Constants.NFragmentStack.push(fragment), name)
                    .commit();
                updateViewedStatus(alNewMainDataList.get(pos).getId(), 1);
                alNewMainDataList.get(pos).setViewed(1);
                adapter.notifyDataSetChanged();
              }
            }
          });

      lvList.setOnItemLongClickListener(
          new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                AdapterView<?> arg0, View arg1, final int pos, long arg3) {
              if (alNewMainDataList != null && alNewMainDataList.get(0) != null) {
                try {
                  AlertDialog.Builder bld = new AlertDialog.Builder(act);
                  bld.setMessage(getString(R.string.deletenews));
                  bld.setNeutralButton(
                      getString(R.string.delete),
                      new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          DeleteNews(pos);
                        }
                      });

                  bld.setPositiveButton(
                      getString(R.string.deleteall),
                      new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          DeleteAllNews();
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
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      section_id = bundle.getString(Constants.SECTIONID);
      new FetchSections_NewsData().execute();
      ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);
    } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  private boolean updateViewedStatus(String id, int newStatus) {
    boolean result = false;
    try {
      String sql =
          String.format(
              Locale.US,
              "UPDATE %s SET %s = %s WHERE (%s = %s) AND  (%s = %s) ",
              Constants.NEWS_TABLE,
              Constants.VIEWD,
              newStatus,
              Constants.ID,
              id,
              Constants.SECTIONID,
              section_id);
      db.execSQL(sql);
      result = true;
    } catch (Exception ex) {
      ex.getMessage();
    }
    return result;
  }

  private Cursor getNewsContentsCursor() {
    Cursor crsr = null;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
      String cDateTime = dateFormat.format(new Date());
      String sql =
          "Select * From ("
              + String.format(
                  Locale.US,
                  "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s =%s  AND  (substr(EndDate , 7,4) || '-' || substr(EndDate ,4,2)  || '-' || substr(EndDate , 1,2) )  "
                      + " >=  (substr(%s , 7,4) || '-' || substr(%s ,4,2)  || '-' || substr(%s , 1,2) )  AND Deleted=0 order by %s DESC ",
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
                  Constants.NEWS_TABLE,
                  Constants.SECTIONID,
                  section_id,
                  cDateTime,
                  cDateTime,
                  cDateTime,
                  " LastChange ")
              + ") order by "
              + Constants.ISFIRST
              + " DESC";
      crsr = db.rawQuery(sql, null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crsr;
  }

  public void DeleteNews(int iPos) {
    try {
      final News_Entity news = alNewMainDataList.get(iPos);
      AlertDialog.Builder bld = new AlertDialog.Builder(act);
      bld.setMessage(getString(R.string.deletenews));
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
              updater.UpdateOldContentsForNews(news.getId());
              alNewMainDataList.remove(news);
              adapter.notifyDataSetChanged();
            }
          });

      bld.create().show();

    } catch (Exception activityException) {
      Log.e("helloandroid dialing example", "Call failed", activityException);
    }
  }

  public void DeleteAllNews() {
    try {
      AlertDialog.Builder bld = new AlertDialog.Builder(act);
      bld.setMessage(getString(R.string.deleteallnews_msg));
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
              updater.UpdateSectionedNews(section_id);
              alNewMainDataList.clear();
              adapter.notifyDataSetChanged();
            }
          });

      bld.create().show();

    } catch (Exception activityException) {
      Log.e("helloandroid dialing example", "Call failed", activityException);
    }
  }

  private class FetchNewsData extends AsyncTask<Void, Void, Void> {
    ArrayList<News_Entity> alNewsData = new ArrayList<News_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        try {
          Cursor crsr = getNewsContentsCursor();
          crsr.moveToFirst();
          if (crsr != null) {
            while (!crsr.isAfterLast()) {
              News_Entity cursorEntity = new News_Entity();
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

              alNewsData.add(cursorEntity);
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
      alNewMainDataList.clear();
      if (alNewsData != null && alNewsData.size() > 0) {
        alNewMainDataList = alNewsData;
      } else if (alNewMainDataList.size() == 0) {
        Utility.showToast(act, getString(R.string.nodata_found));
      }

      adapter.notifyDataSetChanged();
    }
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
      News.alSectionsMainDataList.clear();
      if (alSectionsData != null && alSectionsData.size() > 0) {
        News.alSectionsMainDataList = alSectionsData;
      } else if (News.alSectionsMainDataList.size() == 0) {
        Utility.showToast(act, getString(R.string.nodata_found));
      }

      News.adapter.notifyDataSetChanged();
      new FetchNewsData().execute();
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

      if (alNewMainDataList == null || alNewMainDataList.size() == 0) return contextView;

      LayoutInflater inf = (LayoutInflater) act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
      contextView = inf.inflate(R.layout.listitem, null);
      final ViewHolder holder = new ViewHolder();
      holder.tv_title = (TextView) contextView.findViewById(R.id.tv_title);
      holder.tv_date = (TextView) contextView.findViewById(R.id.tv_date);
      // holder.iv_viewd = (ImageView) contextView.findViewById(R.id.iv_read);
      // holder.iv_status = (ImageView) contextView.findViewById(R.id.iv_status);
      holder.iv_image = (ImageView) contextView.findViewById(R.id.iv_image);
      holder.ln_container = (LinearLayout) contextView.findViewById(R.id.container);
      holder.pd = contextView.findViewById(R.id.screener_pd);

      final News_Entity news = (News_Entity) alNewMainDataList.get(pos);

      if (news != null && news.getTitle() != null) holder.tv_title.setText(news.getTitle());

      if (news != null && news.getStartDate() != null) holder.tv_date.setText(news.getStartDate());
      if (news.getIsFirst() == 1) news.setStatus(17);
      // SetStatusImage(holder.iv_status, news.getStatus());

      if (news.getStatus() == 10 || news.getStatus() == 0) {
        if (news.getViewed() == 0) {
          // holder.iv_status.setVisibility(View.VISIBLE);
        } else {
          // holder.iv_status.setVisibility(View.INVISIBLE);
        }
      }
      if (news.getIsFirst() == 1)
        holder.ln_container.setBackgroundColor(getResources().getColor(R.color.yellow));
      else holder.ln_container.setBackgroundColor(getResources().getColor(R.color.white));

      if (news != null && news.getImage() != null) {
        imageLoader.displayImage(
            act.getString(R.string.news_image_url) + news.getImage(),
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
    private LinearLayout ln_container;
  }

  @Override
  public String getFilter() {
    return "";
  }

  @Override
  public ArrayList<News_Entity> getFilteredList() {
    return alNewMainDataList;
  }

  @Override
  public void onResume() {
    ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
    ((ImageButton) act.getSupportActionBar().getCustomView().findViewById(R.id.btn_refresh))
        .setVisibility(View.VISIBLE);
    act.getSupportActionBar()
        .getCustomView()
        .findViewById(R.id.extra_category)
        .setVisibility(View.GONE);
    act.getSupportActionBar().getCustomView().findViewById(R.id.calendar).setVisibility(View.GONE);
    adapter.notifyDataSetChanged();
    lvList.clearFocus();
    lvList.post(
        new Runnable() {
          @Override
          public void run() {
            lvList.requestFocusFromTouch();
            lvList.setSelection(Pos);
          }
        });
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
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
