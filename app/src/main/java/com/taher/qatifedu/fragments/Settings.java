package com.taher.qatifedu.fragments;
/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.taher.qatifedu.R;
import com.taher.qatifedu.UILApplication;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Settings extends /*Sherlock*/ Fragment /*implements CustomAdapter.ListFiller*/ {
  View myFragmentView;
  private DatabaseHelper dbHelper;
  private SQLiteDatabase db;
  private ContentUpdater updater;
  private /*SherlockFragment*/ AppCompatActivity act;
  private ToggleButton tb_sound;
  private String name = "";
  /*  private ArrayList<Section_Company_Entity> alSection_CompanyDataMain;

  private ListView lvList;
  private CustomAdapter adapter;*/

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    act = /*(SherlockFragmentActivity )*/ (AppCompatActivity) getActivity();
    if (myFragmentView == null) {
      myFragmentView = inflater.inflate(R.layout.setting, container, false);
      // alSection_CompanyDataMain= new ArrayList<Section_Company_Entity>();
      tb_sound = (ToggleButton) myFragmentView.findViewById(R.id.tb_sound);
      tb_sound.setChecked(
          act.getSharedPreferences("sound", Context.MODE_PRIVATE).getBoolean("isSoundOn", true));
      tb_sound.setOnCheckedChangeListener(
          new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              act.getSharedPreferences("sound", Context.MODE_PRIVATE)
                  .edit()
                  .putBoolean("isSoundOn", isChecked)
                  .commit();
            }
          });
      Bundle bundle = this.getArguments();
      name = bundle.getString(Constants.NAME);
      ((TextView) act.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle))
          .setText(name);

      /* lvList = (ListView)myFragmentView.findViewById(R.id.lvList);
      adapter  = new CustomAdapter(act,alSection_CompanyDataMain, R.layout.setting_item, this);
      lvList.setAdapter(adapter);*/

      /*	dbHelper = new DatabaseHelper(act);
      db = dbHelper.getWritableDatabase();
      updater = new ContentUpdater(act, db);
      new FetchSectionsData().execute(new Void[]{null});*/ } else
      ((ViewGroup) myFragmentView).removeView(myFragmentView);
    return myFragmentView;
  }

  /* private Cursor getSectionsContentsCursor() {
  	Cursor crsr = null;
  	try {
  		String[] from = { Constants.ID,Constants.TITLE,Constants.STATUS};
  		crsr = db.query(Constants.SECTIONS_TABLE, from,null, null, null, null,null);
  	} catch (Exception ex) {

  		ex.getMessage();
  	}
  	return crsr;
  }


   private Cursor getCompaniesContentsCursor() {
  	Cursor crsr = null;
  	try {
  		String[] from = { Constants.ID,Constants.TITLE,Constants.STATUS};
  		crsr = db.query(Constants.COMPANIES_TABLE, from,null, null, null, null,null);
  	} catch (Exception ex) {

  		ex.getMessage();
  	}
  	return crsr;
  }

  private class FetchSectionsData extends AsyncTask<Void, Void, Void> {
  	ArrayList<Section_Company_Entity> alSectionData = new ArrayList<Section_Company_Entity>();

  	@Override
  	protected void onPreExecute() {
  		super.onPreExecute();}

  	@Override
  	protected Void doInBackground(Void... params) {
  		try {
  			Cursor crsr = getSectionsContentsCursor();
  			crsr.moveToFirst();
  			if(crsr!=null){
  			while (!crsr.isAfterLast()) {
  				Section_Company_Entity cursorEntity = new Section_Company_Entity();
  				cursorEntity.setId(crsr.getString(0));
  				cursorEntity.setName(crsr.getString(1));
  				cursorEntity.setStatus(Integer.parseInt(crsr.getString(2)));
  				cursorEntity.setType(1);
  				alSectionData.add(cursorEntity);
  				crsr.moveToNext();
  			} crsr.close();
  			}

  			 crsr = getCompaniesContentsCursor();
  			crsr.moveToFirst();
  			if(crsr!=null){
  			while (!crsr.isAfterLast()) {
  				Section_Company_Entity cursorEntity = new Section_Company_Entity();
  				cursorEntity.setId(crsr.getString(0));
  				cursorEntity.setName(crsr.getString(1));
  				cursorEntity.setStatus(Integer.parseInt(crsr.getString(2)));
  				cursorEntity.setType(2);
  				alSectionData.add(cursorEntity);
  				crsr.moveToNext();
  			} crsr.close();
  			}}catch (Exception exception) {
  			return null;
  		}
  		return null;
  	}

  	@Override
  	protected void onPostExecute(Void v) {
  		super.onPostExecute(v);
  		if (alSectionData != null && alSectionData.size() > 0) {
  			alSection_CompanyDataMain = alSectionData;
  			adapter.notifyDataSetChanged();
  		    }}
  }

  @Override
  public void fillListData(View v, int pos) {
  	// TODO Auto-generated method stub

  }


  @Override
  public boolean isEnabled(int pos) {
  	// TODO Auto-generated method stub
  	return false;
  }


  @Override
  public boolean useExtGetView() {
  	return true;
  }



  @Override
  public View getView(int pos, View contextView) {
  try{

  	if (alSection_CompanyDataMain== null || alSection_CompanyDataMain.size() == 0)
  		return  contextView;

  		 LayoutInflater inf = (LayoutInflater)act.getSystemService(act.LAYOUT_INFLATER_SERVICE);
  		 contextView = inf.inflate(R.layout.setting_item, null);
  		 final ViewHolder holder = new ViewHolder();
  		 holder.tb_status = (ToggleButton) contextView.findViewById(R.id.btn_status);
  		 holder.tv_sections = (TextView) contextView.findViewById(R.id.txt_section);

  		 final Section_Company_Entity section = (Section_Company_Entity) alSection_CompanyDataMain.get(pos);
  		 holder.tv_sections.setText(section.getName());
  		 holder.tb_status.setChecked(section.getStatus()!=0);
  		 holder.tb_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {
  				public void onCheckedChanged(CompoundButton buttonView,
  						boolean isChecked) {
  					section.setStatus((isChecked) ? 1 : 0);
  					if(section.getType()==1)
  						updater.updateContentsForSections(section.getId(),section.getStatus());
  					else
  						updater.updateContentsForCompanies(section.getId(),section.getStatus());
  				}});

  }catch (Exception ex){
  	ex.getMessage();
  }
  	return contextView;
  }

  class ViewHolder {
  	private ToggleButton tb_status;
  	private TextView tv_sections;
  }

  @Override
  public String getFilter() {
  	return "";
  }

  @Override
  public ArrayList<Section_Company_Entity> getFilteredList() {
  	return alSection_CompanyDataMain;
  }
  */

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
    super.onResume();
    final Tracker tracker = new UILApplication().getDefaultTracker();

    if (tracker != null) {
      tracker.setScreenName(getClass().getSimpleName());
      tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
  }
}
