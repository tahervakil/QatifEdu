package com.taher.qatifedu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.taher.qatifedu.entity.Sponsor_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;

public class SponsoreActivity extends Activity  {
	ImageLoader imageLoader = ImageLoader.getInstance();
	ArrayList<Sponsor_Entity> alSponsorDataMain;
	private DisplayImageOptions options;
	private ImageView iv_main;
	private View screener;
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	private ContentUpdater updater;
	private String image;
	private TextView tvViews;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sponsore);
		 options = new DisplayImageOptions.Builder()
			.cacheInMemory()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
			screener = findViewById(R.id.screener_pd);
			iv_main = (ImageView) findViewById(R.id.iv_sponserad);
			screener = findViewById(R.id.screener_pd);
			tvViews = (TextView) findViewById(R.id.tv_views);
			iv_main = (ImageView) findViewById(R.id.iv_sponserad);
			iv_main.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (alSponsorDataMain != null && alSponsorDataMain.get(0) != null ) {
						 Intent  intent = new Intent(SponsoreActivity.this,SponsorDetailsActivity.class);
						 intent.putParcelableArrayListExtra("sponsor", alSponsorDataMain);
						 startActivity(intent);	}
				}});
			findViewById(R.id.btn_Go).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				
					startActivity(new Intent(SponsoreActivity.this,MainActivity.class).putExtra("ID",  getIntent().getIntExtra("ID",0)));
					finish();
				}
			});
			dbHelper = new DatabaseHelper(this);
			db = dbHelper.getWritableDatabase();
			updater = new ContentUpdater(this, db);
			new FetchSponsorData().execute(); 
	}

	private Cursor getSponsorContentsCursor() {
		Cursor crsr = null;
		try {
			crsr = db.rawQuery("Select * from "+ Constants.SPONSOR_TABLE, null);
		} catch (Exception ex) {
			
			ex.getMessage();
		}
		return crsr;
	}
	
	private class FetchSponsorData extends AsyncTask<Void, Void, Void> {
		ArrayList<Sponsor_Entity> alSponsorData = new ArrayList<Sponsor_Entity>();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			screener.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				/*if (Utility.isConnected(SponsoreActivity.this)) {
				String strChangeDate=updater.getChangeDate(Constants.SPONSOR_TABLE);
				updater.ContentsForSponsor(new ContentParser("http://qatifedu.com/cmsQatifedu/Services/frmGetSponsor.aspx?lastChange="+strChangeDate).getSponsor());
				 }*/try {	
					Cursor crsr = getSponsorContentsCursor();
					crsr.moveToFirst();
					if(crsr!=null){
					while (!crsr.isAfterLast()) {
						Sponsor_Entity cursorEntity = new Sponsor_Entity();
						cursorEntity.setID((crsr.getString(0)));
						cursorEntity.setName(crsr.getString(1));
						cursorEntity.setImage(crsr.getString(2));
						cursorEntity.setDetails(crsr.getString(3));
						cursorEntity.setEmail(crsr.getString(4));
						cursorEntity.setTel(crsr.getString(5));
						cursorEntity.setWebsite(crsr.getString(6));
						cursorEntity.setNoOfViews(crsr.getString(8));
		
						alSponsorData.add(cursorEntity);
						crsr.moveToNext();
					} crsr.close();
					} }catch (Exception exception) {
					return null;
				}
	             }catch (Exception ex) {
	            	 ex.getMessage();
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			screener.setVisibility(View.GONE);
			
			if (alSponsorData != null && alSponsorData.size() > 0) {
				alSponsorDataMain=alSponsorData;
				Sponsor_Entity Sponsor = alSponsorDataMain.get(0);
				if(Sponsor.getNoOfViews()!=null)
					tvViews.setText(getString(R.string.views)+":"+Sponsor.getNoOfViews());
				image=Sponsor.getImage();
				if (image != null ){
				imageLoader.displayImage("http://qatifedu.com/cmsQatifedu/Upload/Sponsor/"+image,iv_main, options, new SimpleImageLoadingListener() {
		 				@Override
		 				public void onLoadingStarted(String imageUri, View view) {
		 					screener.setVisibility(View.VISIBLE);
		 				}

		 				@Override
		 				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		 					screener.setVisibility(View.GONE);}

		 				@Override
		 				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		 					screener.setVisibility(View.GONE);
							}});}}
	}}
	

	
	
	
	
}

