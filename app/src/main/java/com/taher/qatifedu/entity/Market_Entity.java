package com.taher.qatifedu.entity;
import android.os.Parcel;
import android.os.Parcelable;

public class Market_Entity implements Parcelable{

	private String  id,category_id,user_id,title,text,
	 price,entry_date,file_name1 = null, file_name2 = null, file_name3 = null,
		file_name4 = null,thumb,sold=null,tel=null;

	public Market_Entity(Parcel in) { 
		readFromParcel(in); } 
	
	public Market_Entity( ) { } 
	
	private int viewed;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	public String getEntry_Date() {
		return entry_date;
	}

	public void setEntry_Date(String entry_date) {
		this.entry_date = entry_date;
	}
	
	

	public String getFile_name1() {
		return file_name1;
	}

	public void setFile_name1(String file_name1) {
		this.file_name1 = file_name1;
	}
	
	public String getFile_name2() {
		return file_name2;
	}

	public void setFile_name2(String file_name2) {
		this.file_name2 = file_name2;
	}
	
	public String getFile_name3() {
		return file_name3;
	}

	public void setFile_name3(String file_name3) {
		this.file_name3 = file_name3;
	}
	

	public String getFile_name4() {
		return file_name4;
	}

	public void setFile_name4(String file_name4) {
		this.file_name4 = file_name4;
	}
	
	public String getThump() {
		return thumb;
	}

	public void setThump(String thumb) {
		this.thumb = thumb;
	}
	

	public String getSold() {
		return sold;
	}

	public void setSold(String sold) {
		this.sold = sold;
	}
	
	public int getViewed() {
		return viewed;
	}

	public void setViewed(int viewed) {
		this.viewed = viewed;
	}

	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	 private void readFromParcel(Parcel in) {
	        this.id = in.readString();
	        this.category_id = in.readString();
	        this.user_id = in.readString();
	        this.title = in.readString();
	        this.text = in.readString();
	        this.price = in.readString();
	        this.entry_date = in.readString(); 
	        this.file_name1 = in.readString();
	        this.file_name2 = in.readString();
	        this.file_name3 = in.readString();
	        this.file_name4 = in.readString();
	        this.thumb = in.readString();
	        this.sold = in.readString();
	        this.tel = in.readString();
	        this.viewed = in.readInt();}
	 
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		 dest.writeString(id);
	     dest.writeString(category_id);
	     dest.writeString(user_id);
	     dest.writeString(title);
	     dest.writeString(text);
	     dest.writeString(price);
	     dest.writeString(entry_date);
	     dest.writeString(file_name1);
	     dest.writeString(file_name2);
	     dest.writeString(file_name3);
	     dest.writeString(file_name4);
	     dest.writeString(thumb);
	     dest.writeString(sold);
	     dest.writeString(tel);
	     dest.writeInt(viewed);}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Market_Entity createFromParcel(Parcel in) { 
			return new Market_Entity(in); }   
		
		public Market_Entity[] newArray(int size) { 
			return new Market_Entity[size]; } };
}
