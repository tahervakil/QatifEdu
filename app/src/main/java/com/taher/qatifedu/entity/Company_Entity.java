package com.taher.qatifedu.entity;

import android.os.Parcelable;

import java.io.Serializable;

public class Company_Entity implements Serializable {

  private String id = null;
  private String name = null;
  private String logo = null;
  private String lastChange = null;
  private String lastChangeType = null;
  private int parentId;
  private int unread;
  private int status = 1;
  private int subCategoryType;

  public Company_Entity() {}

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

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public int getSubCategoryType() {
    return subCategoryType;
  }

  public void setSubCategoryType(int subCategoryType) {
    this.subCategoryType = subCategoryType;
  }
}
