package com.taher.qatifedu.utility;

import java.util.ArrayList;

import com.taher.qatifedu.R;
import com.taher.qatifedu.R.drawable;
import com.taher.qatifedu.entity.NavDrawerItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.WINDOW_SERVICE;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        adjustFontScale(context.getResources().getConfiguration());

        if (convertView == null) {
            LayoutInflater mInflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.image);
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        if (position == 0) ivImage.setBackgroundResource(drawable.iv_sections);
        if (position == 1) ivImage.setBackgroundResource(drawable.iv_favorite);
        if (position == 2) ivImage.setBackgroundResource(drawable.more);
        if (position == 3) ivImage.setBackgroundResource(drawable.callus);
        if (position == 4) ivImage.setBackgroundResource(drawable.contactus);
        if (position == 5) ivImage.setBackgroundResource(R.drawable.setting);

        return convertView;
    }

    private void adjustFontScale(Configuration configuration) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        if (configuration.fontScale > 1) {
            configuration.fontScale = (float) 1;
        }

        if (configuration.densityDpi > 480) {
            configuration.densityDpi = 480;
        }

        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getResources().updateConfiguration(configuration, metrics);

    }
}
