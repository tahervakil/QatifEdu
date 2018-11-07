package com.taher.qatifedu.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static int DATABASE_VERSION = 4;
	Context context;

	public DatabaseHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		    if(!checkdatabase()){ 
		    	this.getReadableDatabase();
		        copydatabase(); }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Constants.TAG,
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data"); 
		try{
	   db.execSQL("ALTER TABLE News ADD COLUMN Status INTEGER DEFAULT 0");
		}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE NewsFavorite ADD COLUMN Status INTEGER DEFAULT 0");	}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE Ads ADD COLUMN Status INTEGER DEFAULT 0");	}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE Ads AdsFavorite COLUMN Status INTEGER DEFAULT 0");	}catch(Exception ex){}
	   try{ db.execSQL("ALTER TABLE Sections ADD COLUMN Status INTEGER DEFAULT 1");	}catch(Exception ex){}
	   try{ db.execSQL("ALTER TABLE Companies ADD COLUMN Status INTEGER DEFAULT 1"); 	}catch(Exception ex){}
	   try{ db.execSQL("CREATE TABLE IF NOT EXISTS Sponsor (ID integer NOT NULL PRIMARY KEY,Name text,Image text,Details text,Email text,Phone text,Website text,LastChange text)");
		}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE News ADD COLUMN IsFirst INTEGER DEFAULT 0"); 	}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE Ads ADD COLUMN IsFirst INTEGER DEFAULT 0"); 	}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE News ADD COLUMN Deleted INTEGER DEFAULT 0"); 	}catch(Exception ex){}
	   try{db.execSQL("ALTER TABLE Ads ADD COLUMN Deleted INTEGER DEFAULT 0"); 	}catch(Exception ex){}
		try{ db.execSQL( "Update " + Constants.LATESTCLIENTUPDATE_TABLE +" set " + Constants.CHANGEDATE+" = " + 0 +" WHERE " + Constants.TABLENAME+ "='" + Constants.SECTIONS_TABLE+"'");}catch(Exception ex){}
		try{ db.execSQL( "Update " + Constants.LATESTCLIENTUPDATE_TABLE +" set " + Constants.CHANGEDATE+" = " + 0 +" WHERE " + Constants.TABLENAME+ "='" + Constants.ADS_TABLE+"'");}catch(Exception ex){}
		
		 try{db.execSQL("ALTER TABLE AdsFavorite ADD COLUMN EndDateString text"); 	}catch(Exception ex){}
		 try{db.execSQL("ALTER TABLE Ads ADD COLUMN EndDateString text"); 	}catch(Exception ex){}
		  try{db.execSQL("ALTER TABLE Sponsor ADD COLUMN NoOfViews text"); 	}catch(Exception ex){}
				
	}
	
	
	
	private void copydatabase()  { 
		 try{
	    //Open your local db as the input stream 
	    InputStream myinput =context.getAssets().open(Constants.DATABASE_NAME);
	    //Open the empty db as the output stream 
	    OutputStream myoutput = new FileOutputStream(context.getDatabasePath(Constants.DATABASE_NAME).getPath());
	 
	    // transfer byte to inputfile to outputfile 
	    byte[] buffer = new byte[1024]; 
	    int length; 
	    while ((length = myinput.read(buffer))>0) 
	    { 
	        myoutput.write(buffer,0,length); 
	    } 
	 
	    //Close the streams 
	    myoutput.flush(); 
	    myoutput.close(); 
	    myinput.close(); 
	 
		 }catch(Exception ex){
			 ex.getMessage();
		 }} 
	private boolean checkdatabase() { 
	    //SQLiteDatabase checkdb = null; 
	    boolean checkdb = false; 
	    try{ 
	        String myPath = context.getDatabasePath(Constants.DATABASE_NAME).getPath();
	        File dbfile = new File(myPath); 
	        checkdb = dbfile.exists(); 
	    } 
	    catch(SQLiteException e){ 
	        System.out.println("Database doesn't exist"); 
	    } 
	 
	    return checkdb; 
	} 

}

