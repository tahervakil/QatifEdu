package com.taher.qatifedu;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.taher.qatifedu.entity.NavDrawerItem;
import com.taher.qatifedu.fragments.AddFeedBack;
import com.taher.qatifedu.fragments.Ads;
import com.taher.qatifedu.fragments.ContactUs;
import com.taher.qatifedu.fragments.Favorites;
import com.taher.qatifedu.fragments.InternalBrowser;
import com.taher.qatifedu.fragments.LatestAds_List;
import com.taher.qatifedu.fragments.More;
import com.taher.qatifedu.fragments.Settings;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.NavDrawerListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private ImageButton btnSideDrawer;
  private TextView tvTitle;
  private String[] navMenuTitles;
  int iSelected = 0;
  private ArrayList<NavDrawerItem> navDrawerItems;
  private NavDrawerListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // load slide menu items
    navMenuTitles = getResources().getStringArray(R.array.nav_menu);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

    navDrawerItems = new ArrayList<NavDrawerItem>();
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[6]));
    navDrawerItems.add(new NavDrawerItem(navMenuTitles[7]));

    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
    mDrawerList.setAdapter(adapter);
    mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setCustomView(R.layout.header);
    tvTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);

    btnSideDrawer =
        (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.btn_sidedrawer);
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
      displayView(1);
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
        fragment = new LatestAds_List();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.NFragmentStack.push(fragment);
        break;

      case 1:
        fragment = new Ads();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        data.putInt(Constants.ID, getIntent().getIntExtra("ID", 0));
        fragment.setArguments(data);
        Constants.MFragmentStack.push(fragment);
        /*if(getIntent().getIntExtra("isredirect",0)!=0){
        	fragment = new Ads_Details_WithID();
        	data = new Bundle();
            data.putString(Constants.ID,getIntent().getStringExtra("ID"));
        	fragment.setArguments(data);
        	Constants.MFragmentStack.push(fragment);
        }*/
        break;

      case 2:
        fragment = new Favorites();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.FFragmentStack.push(fragment);
        break;

      case 3:
        fragment = new More();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.MOFragmentStack.push(fragment);
        break;

      case 4:
        try {
          fragment = new InternalBrowser();
          data = new Bundle();
          data.putString("url", "https://play.google.com/store/apps/details?id=com.taher.qatifedu");
          data.putString(Constants.NAME, navMenuTitles[position]);
          data.putInt(Constants.TYPE, 1);
          fragment.setArguments(data);
          Constants.AFragmentStack.push(fragment);
        } catch (Exception e) {
        }
        break;

      case 5:
        fragment = new ContactUs();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.AFragmentStack.push(fragment);
        break;

      case 6:
        fragment = new AddFeedBack();
        data = new Bundle();
        data.putString(Constants.NAME, navMenuTitles[position]);
        fragment.setArguments(data);
        Constants.AFragmentStack.push(fragment);
        break;

      case 7:
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
}
