package com.taher.qatifedu.entity;

public class Event_Entity implements Cloneable {

    private String id;
    private String title;
    private String details;
    private String lastChange;
    private String lastChangeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
