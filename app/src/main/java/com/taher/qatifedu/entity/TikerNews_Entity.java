package com.taher.qatifedu.entity;

public class TikerNews_Entity {
	 //is a class representing the Endpoint resource that has a set of class attributes with corresponding setter and getter functions. 
	private String id,title,lastChange,lastChangeType;

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


}
