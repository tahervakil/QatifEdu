package com.taher.qatifedu.entity;

public class More_Entity {

	private String  id,name,content,lastChange, lastChangeType;
	
	private int type;
	
	public More_Entity(String name,int type){
		this.name=name;
		this.type = type;
	}
	
	public More_Entity(){}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
