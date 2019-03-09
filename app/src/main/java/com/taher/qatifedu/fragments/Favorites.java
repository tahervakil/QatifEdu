package com.taher.qatifedu.fragments;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.utility.Constants;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Favorites extends /*Sherlock*/ Fragment {

  public Favorites() {}

  private ViewPager viewpager;
  private FragmentStatePagerAdapter adapterViewPager;
  private String name;
  private /*SherlockFragment*/ AppCompatActivity act;
  private View rootView;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity) */ (AppCompatActivity) getActivity();
    rootView = inflater.inflate(R.layout.favorite, container, false);
    viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);
    viewpager.setOffscreenPageLimit(2);
    adapterViewPager = new MyPagerAdapter(getChildFragmentManager());

    viewpager.setAdapter(adapterViewPager);
    Bundle bundle = this.getArguments();
    name = bundle.getString(Constants.NAME);
    ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(name);

    return rootView;
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
    viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);
    viewpager.setOffscreenPageLimit(2);
    adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }

  public static class MyPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 1;

    public MyPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    @Override
    public int getCount() {
      return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
      switch (position) {
          /*case 0:
          return  new NewsFavorite_List();
          */
        case 0:
          return new AdsFavorite_List();

        default:
          return null;
      }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    }

    @Override
    public CharSequence getPageTitle(int position) {

      if (position == 0) {

        return "مجتمع";

      } else {

        return "اعلانات";
      }
    }
  }
}
