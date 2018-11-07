package com.taher.qatifedu.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Ads_Entity  implements Parcelable{

	private String  id,section_id,title,text,image,views,startdate,enddate,enddatestring,lastchange,
	 lastchangetype;
	private int viewed,status=0,is_first=0;
	
	public Ads_Entity(Parcel in) { 
		readFromParcel(in); } 
	
	public Ads_Entity( ) { } 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getSectionID() {
		return section_id;
	}

	public void setSectionID(String section_id) {
		this.section_id = section_id;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getStartDate() {
		return startdate;
	}

	public void setStartDate(String startdate) {
		this.startdate = startdate;
	}
	
	public String getEndDate() {
		return enddate;
	}

	public void setEndDate(String enddate) {
		this.enddate = enddate;
	}
	
	public String getLastChange() {
		return lastchange;
	}

	public void setLastChange(String lastchange) {
		this.lastchange = lastchange;
	}

	public String getLastChangeType() {
		return lastchangetype;
	}

	public void setLastChangeType(String lastchangetype) {
		this.lastchangetype = lastchangetype;
	}
	
	
	public int getViewed() {
		return viewed;
	}

	public void setViewed(int viewed) {
		this.viewed = viewed;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getIsFirst() {
		return is_first;
	}

	public void setIsFirst(int is_first) {
		this.is_first = is_first;
	}

	
	public String getEndDateString() {
		return enddatestring;
	}

	public void setEndDateString(String enddatestring) {
		this.enddatestring = enddatestring;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	 private void readFromParcel(Parcel in) {
	        this.id = in.readString();
	        this.section_id = in.readString();
	        this.title = in.readString();
	        this.text = in.readString(); 
	        this.image = in.readString();
	        this.views = in.readString(); 
	        this.startdate = in.readString();
	        this.enddate = in.readString();
	        this.lastchange = in.readString();
	        this.lastchangetype = in.readString();
	        this.viewed = in.readInt();
	        this.status = in.readInt();
	        this.is_first = in.readInt();
	        this.enddatestring = in.readString();}
	 
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		 dest.writeString(id);
	     dest.writeString(section_id);
	     dest.writeString(title);
	     dest.writeString(text);
	     dest.writeString(image);
	     dest.writeString(views);
	     dest.writeString(startdate);
	     dest.writeString(enddate);
	     dest.writeString(lastchange);
	     dest.writeString(lastchangetype);
	     dest.writeInt(viewed);
	     dest.writeInt(status);
	     dest.writeInt(is_first);
	     dest.writeString(enddatestring);}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Ads_Entity createFromParcel(Parcel in) { 
			return new Ads_Entity(in); }   
		
		public Ads_Entity[] newArray(int size) { 
			return new Ads_Entity[size]; } };
}
