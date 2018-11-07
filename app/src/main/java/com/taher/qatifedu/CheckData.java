package com.taher.qatifedu;


import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentParser;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
public class CheckData {
	private Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private ContentUpdater updater;
	
	public  CheckData(Context context){
		this.context=context;
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		new FetchSectionsData().execute();
		new FetchCompaniesData().execute();
		new FetchBannersData().execute();
		new FetchTikerNewsData().execute();
	}

	private class FetchSectionsData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			db = dbHelper.getWritableDatabase();
			updater = new ContentUpdater(context, db);

		}

		@Override
		protected Void doInBackground(Void... params) {
			try {	
				String strChangeDate=updater.getChangeDate(Constants.SECTIONS_TABLE);
				updater.ContentsForSections(strChangeDate);
			} catch (Exception exception) {
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);}
	}
	
	
	private class FetchCompaniesData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			db = dbHelper.getWritableDatabase();
			updater = new ContentUpdater(context, db);

		}

		@Override
		protected Void doInBackground(Void... params) {
			try {	
				String strChangeDate=updater.getChangeDate(Constants.COMPANIES_TABLE);
				updater.ContentsForCompanies(strChangeDate);
				} catch (Exception exception) {
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);}
	}
	
	private class FetchBannersData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();}

		@Override
		protected Void doInBackground(Void... params) {
			try {	
				String strChangeDate=updater.getChangeDate(Constants.BANNER_TABLE);
				updater.ContentsForBanner(new ContentParser(context.getString(R.string.banner_url) + strChangeDate).getBanners());
			} catch (Exception exception) {
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);}
	}
	

	private class FetchTikerNewsData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();}

		@Override
		protected Void doInBackground(Void... params) {
			try {	
				String strChangeDate=updater.getChangeDate(Constants.TIKERNEWS_TABLE);
				updater.ContentsForTikerNews(new ContentParser(context.getString(R.string.tikernews_url) + strChangeDate).getTikerNews());
			} catch (Exception exception) {
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);}
	}
	
	
}
