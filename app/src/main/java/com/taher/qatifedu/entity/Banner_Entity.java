package com.taher.qatifedu.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Banner_Entity implements Parcelable {
	 //is a class representing the Endpoint resource that has a set of class attributes with corresponding setter and getter functions. 
	private String id,title,details,image,
	start_date,expiry_date,lastChange,lastChangeType;

	public Banner_Entity(Parcel in) { 
		readFromParcel(in); } 
	
	public Banner_Entity( ) { } 
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDetials() {
		return details;
	}

	public void setDetials(String details) {
		this.details = details;
	}

	
	
	public String getStart_Date() {
		return start_date;
	}

	public void setStart_Date(String start_date) {
		this.start_date = start_date;
	}
	
	public String getExpiry_Date() {
		return expiry_date;
	}

	public void setExpiry_Date(String expiry_date) {
		this.expiry_date = expiry_date;
	}
	
	public String getLastChange() {
		return lastChange;
	}

	public void setLastChange(String lastChange) {
		this.lastChange = lastChange;
	}
	
	public String getLastChangeType() {
		return lastChangeType;
	}

	public void setLastChangeType(String lastChangeType) {
		this.lastChangeType = lastChangeType;
	}

	 private void readFromParcel(Parcel in) {
	        this.id = in.readString();
	        this.title = in.readString();
	        this.details = in.readString();    
	        this.image = in.readString();
	        this.start_date = in.readString();
	        this.expiry_date = in.readString();
	        this.lastChange = in.readString();}
	 
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		 dest.writeString(id);
	     dest.writeString(title);
	     dest.writeString(details);
	     dest.writeString(image);
	     dest.writeString(start_date);
	     dest.writeString(expiry_date);
	     dest.writeString(lastChange);}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public News_Entity createFromParcel(Parcel in) { 
			return new News_Entity(in); }   
		
		public News_Entity[] newArray(int size) { 
			return new News_Entity[size]; } };

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


}
