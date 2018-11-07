package com.taher.qatifedu;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.taher.qatifedu.entity.Sponsor_Entity;
import com.taher.qatifedu.utility.AsyncTask;
import com.taher.qatifedu.utility.Constants;
import com.taher.qatifedu.utility.ContentParser;
import com.taher.qatifedu.utility.ContentUpdater;
import com.taher.qatifedu.utility.DatabaseHelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	private ContentUpdater updater;
	int iSplashTime = 2000;
	Handler exitHandler = null;
	Runnable exitRunnable = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	    //Backup("/data/data/com.slneeit.safwa/databases/alsafwa",android.os.Environment.getExternalStorageDirectory() + "/Android/data/db");
	     
		dbHelper = new DatabaseHelper(this);
		db = dbHelper.getWritableDatabase();
		updater = new ContentUpdater(this, db);
		new CheckData(SplashActivity.this);	 
		Intent regIntent = new Intent("com.google.android.c2dm.intent.REGISTER");   
    	regIntent.putExtra("app", PendingIntent.getBroadcast(SplashActivity.this, 0, new Intent(), 0));        // Identify role account server will use to send     
    	regIntent.putExtra("sender", "1056793849916");
		try {
			//startService(regIntent);
			startActivity(regIntent);
		}
		catch(Exception e){
			String ee = e.toString();
		};
    	exitHandler = new Handler();
		exitRunnable = new Runnable() {
			public void run(){
				new DemoTask().execute();
				//exitSplash();
			}};
		exitHandler.postDelayed(exitRunnable, iSplashTime);
	   
	}

	/*private void exitSplash(){
		try {
			//if (Utility.isConnected(SplashActivity.this)) {
			
			String strChangeDate=updater.getChangeDate(Constants.SPONSOR_TABLE);
			ArrayList<Sponsor_Entity> sponsor=new ContentParser("http://qatifedu.com/cmsQatifedu/Services/frmGetSponsor.aspx?lastChange="+strChangeDate).getSponsor();
			if(sponsor.size()>0 || getSponsorContentsCursor().getCount()>0){
				
			updater.ContentsForSponsor(sponsor);
			Intent Intent=new Intent(SplashActivity.this,SponsoreActivity.class);
	    	Intent.putExtra("ID",  getIntent().getIntExtra("ID",0));
	    	startActivity(Intent);
			finish();}
			else{	
	        Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
	    	Intent.putExtra("ID",  getIntent().getIntExtra("ID",0));
	    	startActivity(Intent);
			finish();}}
			else{
				    Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
			    	Intent.putExtra("ID", getIntent().getIntExtra("ID",0));
			    	startActivity(Intent);
				    finish();
			}
		} catch (Exception e) {
			   Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
		    	Intent.putExtra("ID", getIntent().getIntExtra("ID",0));
		    	startActivity(Intent);
			    finish();
		}}*/
	private Cursor getSponsorContentsCursor() {
		Cursor crsr = null;
		try {
			crsr = db.rawQuery("Select * from "+ Constants.SPONSOR_TABLE, null);
		} catch (Exception ex) {
			
			ex.getMessage();
		}
		return crsr;
	}
	
	  public static void Backup(String InputPath,String OutputPath)  { 
	    	try{
	        //Open your local db as the input stream 
	        String inFileName = InputPath; 
	        File dbFile = new File(inFileName); 
	        FileInputStream fis = new FileInputStream(dbFile); 
	        String outFileName =  OutputPath; 
	        //Open the empty db as the output stream 
	        OutputStream output = new FileOutputStream(outFileName); 
	        //transfer bytes from the inputfile to the outputfile 
	        byte[] buffer = new byte[1024]; 
	        int length; 
	        while ((length = fis.read(buffer))>0){ 
	            output.write(buffer, 0, length); 
	        } 
	        //Close the streams 
	        output.flush(); 
	        output.close(); 
	        fis.close();
	        
	    	}catch(Exception ex){
	    		ex.getMessage();
	    	}}
	  
	  class DemoTask extends AsyncTask<Void, Void, Void> {

		   
		    protected void onPostExecute(Void result) {
		        // TODO: do something with the feed
		    }

			@Override
			protected Void doInBackground(Void... params) {
				try {
					String strView =getNumberView();
					String strChangeDate=updater.getChangeDate(Constants.SPONSOR_TABLE);
					//new ContentParser("http://qatifedu.com/cmsQatifedu/Services/frmSponsorView.aspx?lastChange="+strChangeDate).getSponsorView();
					ArrayList<Sponsor_Entity> sponsor=new ContentParser("http://qatifedu.com/cmsQatifedu/Services/frmGetSponsor.aspx?lastChange="+strChangeDate).getSponsor();
					if(sponsor.size()>0 || getSponsorContentsCursor().getCount()>0){
					updater.updateSponsorNoView(strView);
					updater.ContentsForSponsor(sponsor,strView);
					Intent Intent=new Intent(SplashActivity.this,SponsoreActivity.class);
			    	Intent.putExtra("ID",  getIntent().getIntExtra("ID",0));
			    	startActivity(Intent);
					finish();}
					else{	
			        Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
			    	Intent.putExtra("ID",  getIntent().getIntExtra("ID",0));
			    	startActivity(Intent);
					finish();}/*}
					else{
						    Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
					    	Intent.putExtra("ID", getIntent().getIntExtra("ID",0));
					    	startActivity(Intent);
						    finish();
					}*/
				} catch (Exception e) {
					   Intent Intent=new Intent(SplashActivity.this,MainActivity.class);
				    	Intent.putExtra("ID", getIntent().getIntExtra("ID",0));
				    	startActivity(Intent);
					    finish();
				}
				return null;
			}
		}
	  
	  private String getNumberView(){
		  String NViews="0";
		  try {

	            URL mUrl = new URL("http://qatifedu.com/cmsQatifedu/Services/frmSponsorView.aspx?");
	            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
	            httpConnection.setRequestMethod("GET");
	            httpConnection.setRequestProperty("Content-length", "0");
	            httpConnection.setUseCaches(false);
	            httpConnection.setAllowUserInteraction(false);
	            httpConnection.setConnectTimeout(100000);
	            httpConnection.setReadTimeout(100000);

	            httpConnection.connect();

	            int responseCode = httpConnection.getResponseCode();

	            if (responseCode == HttpURLConnection.HTTP_OK) {
	                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line + "\n");
	                }
	                br.close();
	                return sb.toString();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		return NViews;
		  
		  
	  }
	    
	 
}
