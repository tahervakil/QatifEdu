package com.taher.qatifedu.fragments;

import java.util.ArrayList;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.entity.ContactUs_Entity;
import com.taher.qatifedu.entity.More_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentParser;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class More extends /*Sherlock*/ Fragment {
  MoreListAdapter adapter;
  View myFragmentView;
  ListView lvList;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private /*SherlockFragment*/ AppCompatActivity act;
  ArrayList<More_Entity> alMoreDataMain;
  // ArrayList<ContactUs_Entity> alContactUsDataMain;
  private String[] more;
  private ContentUpdater updater;
  private String name = "";

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity)*/ (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
      myFragmentView = inflater.inflate(R.layout.more, container, false);
      lvList = (ListView) myFragmentView.findViewById(R.id.lvList);
      alMoreDataMain = new ArrayList<More_Entity>();
      // alContactUsDataMain= new ArrayList<ContactUs_Entity>();

      adapter = new MoreListAdapter();
      lvList.setAdapter(adapter);
      lvList.setOnItemClickListener(
          new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentView, View childView, int pos, long id) {
              if (alMoreDataMain != null && alMoreDataMain.get(pos) != null) {
                // if (alMoreDataMain.get(pos).getType() == 1) {
                /*switch (pos) {
                case 0:
                  */
                /*if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                  try {
                    AlertDialog.Builder bld = new AlertDialog.Builder(act);
                    bld.setMessage(getString(R.string.phonecall_msg));
                    bld.setNegativeButton(
                        getString(R.string.cancel),
                        new android.content.DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {}
                        });
                    bld.setPositiveButton(
                        getString(R.string.submit),
                        new android.content.DialogInterface.OnClickListener() {

                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            boolean hasTelephony =
                                act.getPackageManager()
                                    .hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                            if (hasTelephony) {
                              Intent callIntent = new Intent(Intent.ACTION_CALL);
                              String phonecall = alContactUsDataMain.get(0).getPhone();
                              if (!phonecall.contains("tel:")) phonecall = "tel:" + phonecall;
                              callIntent.setData(Uri.parse(phonecall));
                              startActivityForResult(callIntent, 1);
                            }
                          }
                        });

                    bld.create().show();

                  } catch (Exception activityException) {
                    Log.e("helloandroid dialing example", "Call failed", activityException);
                  }
                }*/
                /*
                    break;

                  case 1:
                    if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                      try {

                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
                        intent.putExtra(
                            android.content.Intent.EXTRA_EMAIL,
                            new String[] {alContactUsDataMain.get(0).getEmail()});
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                        startActivityForResult(Intent.createChooser(intent, "Send mail.."), 1);
                      } catch (Exception e) {
                      }
                    }
                    break;

                  case 2:
                    if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                      try {
                        String url = alContactUsDataMain.get(0).getWebsite();
                        if (!url.contains("http://") && !url.contains("https://"))
                          url = "http://" + url;
                        OpenSite(url, "الموقع الالكترونى");
                      } catch (Exception e) {
                      }
                    }
                    break;

                  case 3:
                    if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                      try {
                        String url = alContactUsDataMain.get(0).getTwitter();
                        if (!url.contains("https://m.twitter.com/"))
                          url = "https://m.twitter.com/" + url;
                        OpenSite(url, "تويتر");
                      } catch (Exception e) {
                      }
                    }
                    break;

                  case 4:
                    if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                      try {
                        String url = alContactUsDataMain.get(0).getInstegram();
                        if (!url.contains("http://instagram.com/"))
                          url = "http://instagram.com/" + url;
                        OpenSite(url, "انستغرام");
                      } catch (Exception e) {
                      }
                    }
                    break;

                  case 5:
                    if (alContactUsDataMain != null && alContactUsDataMain.get(0) != null) {
                      try {
                        String url = alContactUsDataMain.get(0).getYoutube();
                        if (!url.contains("http://youtube.com/"))
                          url = "http://youtube.com/" + url;
                        OpenSite(url, "يوتيوب");
                      } catch (Exception e) {
                      }
                    }
                    break;
                }
                }*/
                if (alMoreDataMain.get(pos).getType() == 2) {
                  try {
                    if (alMoreDataMain.get(pos).getContent() != null) {
                      String url = alMoreDataMain.get(pos).getContent();
                      OpenSite(url, alMoreDataMain.get(pos).getName());
                    }
                  } catch (Exception e) {
                  }
                } else if (alMoreDataMain.get(pos).getType() == 3) {
                  try {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody =
                        "http://qatifedu.com/cmsQatifedu/index1.htm \nشاركنا و استفيد من خدمات التطبيق";
                    sharingIntent.putExtra(
                        android.content.Intent.EXTRA_SUBJECT, "مشاركة تطبيق تعليم القطيف");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), 1);
                  } catch (Exception e) {
                  }
                }
              }
            }
          });
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);
      more = getResources().getStringArray(R.array.more_items);
      /*alMoreDataMain.add(new More_Entity(more[0], 1));
      alMoreDataMain.add(new More_Entity(more[1], 1));
      alMoreDataMain.add(new More_Entity(more[2], 1));
      alMoreDataMain.add(new More_Entity(more[3], 1));
      alMoreDataMain.add(new More_Entity(more[4], 1));
      alMoreDataMain.add(new More_Entity(more[5], 1));*/
      alMoreDataMain.add(new More_Entity(more[0], 0));
      alMoreDataMain.add(new More_Entity(more[1], 3));

      dbHelper = new DatabaseHelper(act);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(act, db);
      if (Utility.isConnected(act)) new Fetch_ContactUsData().execute();
      else Utility.showToast(act, getString(R.string.alert_need_internet_connection));
      new FetchGenerlaData().execute();
    } else ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
  }

  public void OpenSite(String url, String name) {
    Bundle data = new Bundle();
    InternalBrowser fragment = new InternalBrowser();
    data.putString("url", url);
    data.putString(Constants.NAME, name);
    data.putInt(Constants.TYPE, 1);
    fragment.setArguments(data);
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    if (getFragmentManager().findFragmentByTag(name) == null) ;
    ft.addToBackStack(null);
    ft.replace(R.id.frame_container, Constants.MOFragmentStack.push(fragment), name).commit();
  }

  private Cursor getGeneralContentsCursor() {
    Cursor crsr = null;
    try {
      String[] from = {Constants.ID, Constants.NAME, Constants.CONTENT};
      crsr = db.query(Constants.GENERAL_TABLE, from, null, null, null, null, null);
    } catch (Exception ex) {
    }
    return crsr;
  }

  private class FetchGenerlaData extends AsyncTask<Void, Void, Void> {
    ArrayList<More_Entity> alGeneralData = new ArrayList<More_Entity>();

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        String strChangeDate = updater.getChangeDate(Constants.GENERAL_TABLE);
        updater.ContentsForGeneral(
            new ContentParser(act.getString(R.string.general_url) + strChangeDate)
                .getGeneralContents());
        Cursor crsr = getGeneralContentsCursor();
        crsr.moveToFirst();
        if (crsr != null) {
          while (!crsr.isAfterLast()) {
            More_Entity cursorEntity = new More_Entity();
            cursorEntity.setId((crsr.getString(0)));
            cursorEntity.setName(crsr.getString(1));
            cursorEntity.setContent(crsr.getString(2));
            cursorEntity.setType(2);
            alGeneralData.add(cursorEntity);
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
      if (alGeneralData != null && alGeneralData.size() > 0) {
        alMoreDataMain.add(new More_Entity(more[1], 0));
        alMoreDataMain.addAll(alGeneralData);
      } else alMoreDataMain.add(new More_Entity(more[1], 0));
      adapter.notifyDataSetChanged();
    }
  }

  private class Fetch_ContactUsData extends AsyncTask<Void, Void, ContactUs_Entity> {

    private ProgressDialog progress;

    @Override
    protected void onPreExecute() {
      progress = ProgressDialog.show(act, getString(R.string.pleaseWait), "", true, true, null);
      super.onPreExecute();
    }

    @Override
    protected ContactUs_Entity doInBackground(Void... params) {

      try {
        return new ContentParser("http://qatifedu.com/cmsQatifedu/Services/xmlContact.xml")
            .getContactUsContents();
      } catch (Exception exception) {
        return null;
      }
    }

    @Override
    protected void onPostExecute(ContactUs_Entity ContactUs) {
      super.onPostExecute(ContactUs);
      progress.dismiss();
      // if (ContactUs != null) alContactUsDataMain.add(ContactUs);
    }
  }

  private class MoreListAdapter extends BaseAdapter {

    public MoreListAdapter() {}

    @Override
    public int getCount() {
      return alMoreDataMain.size();
    }

    @Override
    public Object getItem(int arg0) {
      return alMoreDataMain.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
      return 0;
    }

    @Override
    public int getItemViewType(int position) {
      return alMoreDataMain.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      int type = getItemViewType(position);
      final ViewHolder holder = new ViewHolder();
      ;
      LayoutInflater mInflater =
          (LayoutInflater) act.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
      if (type == 0) {
        convertView = mInflater.inflate(R.layout.more_header_item, null);
        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
      } else {
        convertView = mInflater.inflate(R.layout.more_item, null);
        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
      }

      More_Entity more = alMoreDataMain.get(position);

      if (more != null && more.getName() != null) holder.tv_title.setText(more.getName());

      return convertView;
    }

    class ViewHolder {
      private TextView tv_title;
    }
  }

  @Override
  public void onResume() {
    ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);
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
}
