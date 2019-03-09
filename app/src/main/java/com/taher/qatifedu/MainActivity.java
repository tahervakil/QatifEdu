package com.taher.qatifedu;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.entity.Company_Entity;
import com.taher.qatifedu.entity.Event_Entity;
import com.taher.qatifedu.entity.NavDrawerItem;
import com.taher.qatifedu.fragments.AddFeedBack;
import com.taher.qatifedu.fragments.Ads;
import com.taher.qatifedu.fragments.ContactUs;
import com.taher.qatifedu.fragments.EventDisplay;
import com.taher.qatifedu.fragments.Favorites;
import com.taher.qatifedu.fragments.InternalBrowser;
import com.taher.qatifedu.fragments.LatestAds_List;
import com.taher.qatifedu.fragments.More;
import com.taher.qatifedu.fragments.Settings;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.CustomTextView;
import com.taher.qatifedu.utility.DatabaseHelper;
import com.taher.qatifedu.utility.GoogleAnalyticsApplication;
import com.taher.qatifedu.utility.NavDrawerListAdapter;
import com.taher.qatifedu.utility.Utility;

import java.util.ArrayList;
import java.util.Locale;

import static com.taher.qatifedu.utility.Constants.TAG;

public class MainActivity extends AppCompatActivity {
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private ImageButton btnSideDrawer;
  private ImageButton extraCategory;
  private ImageButton calendar;
  private TextView tvTitle;
  private String[] navMenuTitles;
  int iSelected = 0;
  private ArrayList<NavDrawerItem> navDrawerItems;
  private NavDrawerListAdapter adapter;
  private Tracker tracker;
  public static ArrayList<Company_Entity> alCompaniesMainDataList;
  private ArrayList<Event_Entity> eventEntityMainData;
  private AppCompatActivity appCompatActivity;
  private ContentUpdater updater;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;

  @Override
  protected void onResume() {
    super.onResume();
    tracker.setScreenName(getClass().getSimpleName());
    tracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  public void adjustFontScale(Configuration configuration) {

    DisplayMetrics metrics = getResources().getDisplayMetrics();

    /*DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(metrics);*/

    if (configuration.fontScale > 1 || configuration.fontScale < 1) {
      configuration.fontScale = (float) 1;
    }

    /*if (configuration.densityDpi > 480 || configuration.densityDpi < 480) {
        configuration.densityDpi = 480;
    }*/

    metrics.scaledDensity = configuration.fontScale * metrics.density;
    getBaseContext().getResources().updateConfiguration(configuration, metrics);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    adjustFontScale(getResources().getConfiguration());

    setContentView(R.layout.activity_main);

    eventEntityMainData = new ArrayList<>();

    dbHelper = new DatabaseHelper(getApplicationContext());
    db = dbHelper.getWritableDatabase();

    updater = new ContentUpdater(getApplicationContext(), db);
    // load slide menu items
    navMenuTitles = getResources().getStringArray(R.array.nav_menu);
    mDrawerLayout = findViewById(R.id.drawer_layout);
    mDrawerList = findViewById(R.id.list_slidermenu);

    navDrawerItems = new ArrayList<>();
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));

    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
    mDrawerList.setAdapter(adapter);
    mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayShowTitleEnabled(false);

    new FetchEvent_Data().execute();

    ActionBar.LayoutParams layoutParams =
        new ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    View view = getLayoutInflater().inflate(R.layout.header, null);

    adjustFontScale(view.getResources().getConfiguration());
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setCustomView(view, layoutParams);
    Toolbar parent = (Toolbar) view.getParent();
    parent.setContentInsetsAbsolute(0, 0);

    UILApplication application = (UILApplication) getApplication();
    tracker = application.getDefaultTracker();

    alCompaniesMainDataList = new ArrayList<>();

    tvTitle = getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
    extraCategory = getSupportActionBar().getCustomView().findViewById(R.id.extra_category);
    calendar = getSupportActionBar().getCustomView().findViewById(R.id.calendar);

    extraCategory.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ArrayList<Event_Entity> currentEvent = null;
            currentEvent = eventEntityMainData;

            String name = "Event";
            Log.i(TAG, "RECORD: " + currentEvent.get(0).getTitle());
            /*Bundle data = new Bundle();
            data.putString(Constants.ID, currentEvent.get(0).getId());
            data.putString(Constants.TITLE, currentEvent.get(0).getTitle());
            data.putString(Constants.DETAILS, currentEvent.get(0).getDetails());*/
            Fragment fragment = new EventDisplay();
            // fragment.setArguments(data);

            FragmentTransaction fragmentTransaction =
                MainActivity.this.getSupportFragmentManager().beginTransaction();
            if (getFragmentManager().findFragmentByTag(name) == null) ;
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction
                .replace(R.id.frame_container, Constants.MFragmentStack.push(fragment), "Event")
                .commit();
          }
        });

    btnSideDrawer = getSupportActionBar().getCustomView().findViewById(R.id.btn_sidedrawer);
    btnSideDrawer.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
              mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
              mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
          }
        });

    mDrawerToggle =
        new ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            R.drawable.menu, // nav menu toggle icon
            R.string.app_name, // nav drawer open - description for accessibility
            R.string.app_name // nav drawer close - description for accessibility
            ) {
          public void onDrawerClosed(View view) {
            closeOptionsMenu();
          }

          public void onDrawerOpened(View drawerView) {
            closeOptionsMenu();
          }
        };
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    if (savedInstanceState == null) {
      // on first time display view for first nav item
      displayView(0);
    }
  }

  /** Slide menu item click listener */
  private class SlideMenuClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      // display view for selected nav drawer item
      displayView(position);
    }
  }

  /** Displaying fragment view for selected nav drawer list item */
  private void displayView(int position) {
    // update the main content by replacing fragments

    Fragment fragment = null;
    Bundle data = null;
    switch (position) {
      case 0:
        fragment = new Ads();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        data.putInt(Constants.ID, getIntent().getIntExtra("ID", 0));
        fragment.setArguments(data);
        Constants.MFragmentStack.push(fragment);
        break;

      case 1:
        fragment = new Favorites();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.FFragmentStack.push(fragment);
        break;

      case 2:
        fragment = new More();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.MOFragmentStack.push(fragment);
        break;

      case 3:
        fragment = new ContactUs();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.AFragmentStack.push(fragment);
        break;

      case 4:
        fragment = new AddFeedBack();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.AFragmentStack.push(fragment);
        break;

      case 5:
        fragment = new Settings();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.AFragmentStack.push(fragment);
        break;

      default:
        break;
    }
    if (fragment != null) {
      FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.frame_container, fragment);
      if (this.getSupportFragmentManager().findFragmentByTag(navMenuTitles[position]) == null) ;
      ft.addToBackStack(null);
      ft.commit();
      iSelected = position;
      mDrawerList.setItemChecked(position, true);
      mDrawerList.setSelection(position);
      mDrawerLayout.closeDrawer(mDrawerList);
      tvTitle.setText(navMenuTitles[position]);
    } else {
      // error in creating fragment
      Log.e("MainActivity", "Error in creating fragment");
    }
  }

  @Override
  public void onBackPressed() {
    if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
      this.finish();
    } else {
      getSupportFragmentManager().popBackStack();
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggls
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  private class FetchEvent_Data extends AsyncTask<Void, Void, Void> {

    ArrayList<Event_Entity> event_entities = new ArrayList<>();

    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = new ProgressDialog(getApplicationContext());
      if (event_entities != null) {
        eventEntityMainData = event_entities;
      }
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        try {

          if (Utility.isConnected(getApplicationContext())) {
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
      progressDialog.dismiss();
      if (event_entities != null) {
        eventEntityMainData = event_entities;
        Log.i(TAG, "COME HERE " + eventEntityMainData.get(0).getTitle());
      }
    }
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
}
