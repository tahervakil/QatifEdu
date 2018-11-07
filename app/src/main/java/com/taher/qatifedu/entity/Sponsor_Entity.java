package com.taher.qatifedu.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Sponsor_Entity   implements Parcelable {

	private String id,name,image,details,email,tel,
	 website,lastchange,lastchangeType,noofviews;

	
	public Sponsor_Entity(Parcel in) { 
		readFromParcel(in); } 
	
	public Sponsor_Entity( ) { } 
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
	
	public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}


	
	public String getLastChange() {
		return lastchange;
	}

	public void setLastChange(String lastchange) {
		this.lastchange = lastchange;
	}
	
	public String getLastChangeType() {
		return lastchangeType;
	}

	public void setLastChangeType(String lastchangeType) {
		this.lastchangeType = lastchangeType;
	}
	
	public String getNoOfViews() {
		return noofviews;
	}

	public void setNoOfViews(String noofviews) {
		this.noofviews = noofviews;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	 private void readFromParcel(Parcel in) {
	        this.id = in.readString();
	        this.name = in.readString();
	        this.image = in.readString();
	        this.details = in.readString(); 
	        this.email = in.readString();
	        this.tel = in.readString(); 
	        this.website = in.readString();
	        this.lastchange = in.readString();
	        this.lastchangeType = in.readString();
			this.noofviews = in.readString();}
	 
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		 dest.writeString(id);
	     dest.writeString(name);
	     dest.writeString(image);
	     dest.writeString(details);
	     dest.writeString(email);
	     dest.writeString(tel);
	     dest.writeString(website);
	     dest.writeString(lastchange);
	     dest.writeString(lastchangeType);
		 dest.writeString(noofviews);}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Sponsor_Entity createFromParcel(Parcel in) { 
			return new Sponsor_Entity(in); }   
		
		public Sponsor_Entity[] newArray(int size) { 
			return new Sponsor_Entity[size]; } };

}
