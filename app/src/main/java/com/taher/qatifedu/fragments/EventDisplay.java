package com.taher.qatifedu.fragments;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.R;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

import static com.taher.qatifedu.utility.Constants.TAG;

public class EventDisplay extends Fragment {

    View myFragmentView;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ContentUpdater updater;
    private AppCompatActivity appCompatActivity;
    private int width = 0, height = 0, x, x2, total_positive, total_positive2;
    HorizontalScrollView layout;
    private DisplayImageOptions options;
    private ProgressDialog progress = null;
    private ArrayList<Banner_Entity> alBannersMainDataList;
    private LinearLayout ln_Banner;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Handler handler;
    private TextView event_title;
    private WebView event_webView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        appCompatActivity = (AppCompatActivity) getActivity();

        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.event_detail, container, false);
            Display localDisplay = getActivity().getWindowManager().getDefaultDisplay();
            width = 100 * localDisplay.getWidth() / 100;
            height = 17 * localDisplay.getHeight() / 100;
            layout = myFragmentView.findViewById(R.id.scroll_layout);

            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
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
            alBannersMainDataList = new ArrayList<>();
            ln_Banner = myFragmentView.findViewById(R.id.banner_layout);
            event_title = myFragmentView.findViewById(R.id.eventTitle);
            event_webView = myFragmentView.findViewById(R.id.event_web_view);

            dbHelper = new DatabaseHelper(getContext());
            db = dbHelper.getWritableDatabase();
            updater = new ContentUpdater(getContext(), db);

            ((TextView)
                    appCompatActivity.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
                    .setText("Event");

            Bundle bundle = new Bundle();
            bundle = getArguments();
            event_title.setText(bundle.getString(Constants.TITLE));

            Log.i(TAG, "WEB VIEW URL: " + bundle.getString(Constants.DETAILS));

            event_webView.getSettings().setBuiltInZoomControls(true);
            event_webView.getSettings().setDefaultTextEncodingName("utf-8");
            event_webView.loadDataWithBaseURL(null, bundle.getString(Constants.DETAILS), "text/html", "UTF-8", null);

            // Haven't implemented code for multiple events. Everything else is coded.
            // Just get list of events from Ads class and then rest can be copied from Ads_details.

        } else {
            ((ViewGroup) myFragmentView).removeView(myFragmentView);
        }

        return myFragmentView;
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
            //progress.dismiss();
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
            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(width, height);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setLayoutParams(param);
            image.setTag(i);

            image.setOnClickListener(
                    new View.OnClickListener() {

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
}
