package com.taher.qatifedu.entity;

public class Section_Entity {

	private String id = null , name = null,logo=null,lastChange=null, 
	 lastChangeType=null;
	

	private int unread,status=1;
	public Section_Entity() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
