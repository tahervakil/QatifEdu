package com.taher.qatifedu.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import com.taher.qatifedu.R;
import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.entity.Company_Entity;
import com.taher.qatifedu.entity.More_Entity;
import com.taher.qatifedu.entity.News_Entity;
import com.taher.qatifedu.entity.Section_Entity;
import com.taher.qatifedu.entity.Sponsor_Entity;
import com.taher.qatifedu.entity.TikerNews_Entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContentUpdater {
  private Context context;
  private SQLiteDatabase db;

  public ContentUpdater(Context context, SQLiteDatabase db) {
    this.context = context;
    this.db = db;
  }

  public synchronized String getChangeDate(String strTableName) {
    String strChangeDate = "0";
    try {

      Cursor cursor =
          db.rawQuery(
              "select "
                  + Constants.CHANGEDATE
                  + " from "
                  + Constants.LATESTCLIENTUPDATE_TABLE
                  + " where "
                  + Constants.TABLENAME
                  + "='"
                  + strTableName
                  + "'",
              null);
      if (cursor == null) return strChangeDate;

      try {
        if (cursor.moveToFirst()) strChangeDate = cursor.getString(0);
      } catch (Exception ex) {
        cursor.close();
      }
      return strChangeDate;
    } catch (SQLException e) {
      Log.e(Constants.TAG, "An exception was thrown", e);
      return strChangeDate;
    }
  }

  public synchronized void updateLatestClientUpdate(String strChangeDate, String strTableName) {

    try {
      String sql =
          "Update "
              + Constants.LATESTCLIENTUPDATE_TABLE
              + " set "
              + Constants.CHANGEDATE
              + " = "
              + strChangeDate
              + " WHERE "
              + Constants.TABLENAME
              + "='"
              + strTableName
              + "'";
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void ContentsForGeneral(ArrayList<More_Entity> contents) {
    try {

      if (contents != null && contents.size() > 0) {
        for (More_Entity c : contents) {
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForGeneral(c);
              break;

            case 2:
              deleteOldContentsForGeneral(c.getId());
              saveContentsForGeneral(c);
              break;

            case 3:
              deleteOldContentsForGeneral(c.getId());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForGeneral(More_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getName() != null && !c.getName().equals("")) vals.put(Constants.NAME, c.getName());
      if (c.getContent() != null && !c.getContent().equals(""))
        vals.put(Constants.CONTENT, c.getContent());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());

      long rid = db.insert(Constants.GENERAL_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForGeneral(String Id) {

    try {
      String sql = "DELETE FROM " + Constants.GENERAL_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteAllOldContentsForGeneral() {

    try {
      String sql = "DELETE FROM " + Constants.GENERAL_TABLE;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void ContentsForSections(String strChangeDate) {
    try {
      ArrayList<Section_Entity> contents = null;
      contents = downloadRecentContentsForSections(strChangeDate);
      if (contents != null && contents.size() > 0) {
        for (Section_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.SECTIONS_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForSections(c);
              break;

            case 2:
              c.setStatus(getSectionStatus(c.getId()));
              deleteOldContentsForSections(c.getId());
              saveContentsForSections(c);
              break;

            case 3:
              deleteOldContentsForSections(c.getId());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForSections(Section_Entity c) {
    try {

      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getName() != null && !c.getName().equals("")) vals.put(Constants.TITLE, c.getName());
      if (c.getLogo() != null && !c.getLogo().equals("")) vals.put(Constants.LOGO, c.getLogo());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      long rid = db.insert(Constants.SECTIONS_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public void updateContentsForSections(String strID, int iStatus) {
    try {

      ContentValues vals = new ContentValues();
      vals.put(Constants.STATUS, iStatus);
      long rid = db.update(Constants.SECTIONS_TABLE, vals, Constants.ID + "= " + strID, null);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + strID;
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForSections(String Id) {

    try {
      String sql = "DELETE FROM " + Constants.SECTIONS_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public int getSectionStatus(String strsection) {
    int Status = 1;
    try {
      String sql =
          "Select"
              + Constants.STATUS
              + " FROM "
              + Constants.SECTIONS_TABLE
              + " Where  "
              + Constants.ID
              + " ="
              + strsection;
      Cursor crsr = db.rawQuery(sql, null);
      crsr.moveToNext();
      Status = crsr.getInt(0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return Status;
  }

  private ArrayList<Section_Entity> downloadRecentContentsForSections(String strChangeDate)
      throws IOException, ParserConfigurationException, SAXException {
    return new ContentParser(context.getString(R.string.sections_url) + strChangeDate)
        .getSectionContents();
  }

  public synchronized void ContentsForCompanies(String strChangeDate) {
    try {
      ArrayList<Company_Entity> contents = null;
      contents = downloadRecentContentsForCompanies(strChangeDate);

      if (contents != null && contents.size() > 0) {
        for (Company_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.COMPANIES_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForCompanies(c);
              break;

            case 2:
              c.setStatus(getCompanyStatus(c.getId()));
              deleteOldContentsForCompanies(c.getId());
              saveContentsForCompanies(c);
              break;

            case 3:
              deleteOldContentsForCompanies(c.getId());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForCompanies(Company_Entity c) {
    try {

      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getName() != null && !c.getName().equals("")) vals.put(Constants.TITLE, c.getName());
      if (c.getLogo() != null && !c.getLogo().equals("")) vals.put(Constants.LOGO, c.getLogo());
      if (c.getParentId() != 0) vals.put(Constants.PARENTID, c.getParentId());
      if (c.getSubCategoryType() != 0)
        vals.put(Constants.SUB_CATEGORY_TYPE, c.getSubCategoryType());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      long rid = db.insert(Constants.COMPANIES_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForCompanies(String Id) {

    try {
      String sql = "DELETE FROM " + Constants.COMPANIES_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void updateContentsForCompanies(String strID, int iStatus) {
    try {

      ContentValues vals = new ContentValues();
      vals.put(Constants.STATUS, iStatus);
      long rid = db.update(Constants.COMPANIES_TABLE, vals, Constants.ID + "= " + strID, null);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + strID;
      }

    } catch (Exception ex) {
    }
  }

  public int getCompanyStatus(String strcompany) {
    int Status = 1;
    try {
      String sql =
          "Select "
              + Constants.STATUS
              + " FROM "
              + Constants.COMPANIES_TABLE
              + " Where  "
              + Constants.ID
              + " ="
              + strcompany;
      Cursor crsr = db.rawQuery(sql, null);
      crsr.moveToNext();
      Status = crsr.getInt(0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return Status;
  }

  private ArrayList<Company_Entity> downloadRecentContentsForCompanies(String strChangeDate)
      throws IOException, ParserConfigurationException, SAXException {
    return new ContentParser(context.getString(R.string.companies_url) + strChangeDate)
        .getCompaniesContents();
  }

  public synchronized void ContentsForBanner(ArrayList<Banner_Entity> contents) {
    try {
      // deleteOldContentsForBanner();
      if (contents != null && contents.size() > 0) {
        for (Banner_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.BANNER_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForBanner(c);
              break;

            case 2:
              deleteOldContentsForBanner(c.getID());
              saveContentsForBanner(c);
              break;

            case 3:
              deleteOldContentsForBanner(c.getID());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForBanner(Banner_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getID());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getDetials() != null && !c.getDetials().equals(""))
        vals.put(Constants.DETIALS, c.getDetials());
      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());
      if (c.getStart_Date() != null && !c.getStart_Date().equals(""))
        vals.put(Constants.STARTDATE, c.getStart_Date());
      if (c.getExpiry_Date() != null && !c.getExpiry_Date().equals(""))
        vals.put(Constants.EXPIRYDATE, c.getExpiry_Date());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());

      long rid = db.insert(Constants.BANNER_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForBanner(String bannerId) {

    try {
      String sql =
          "DELETE FROM " + Constants.BANNER_TABLE + " WHERE " + Constants.ID + "=" + bannerId;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForBanner() {

    try {
      String sql = "DELETE FROM " + Constants.BANNER_TABLE;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void ContentsForTikerNews(ArrayList<TikerNews_Entity> contents) {
    try {
      // deleteOldContentsForBanner();
      if (contents != null && contents.size() > 0) {
        for (TikerNews_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.TIKERNEWS_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForTikerNews(c);
              break;

            case 2:
              deleteOldContentsForTikerNews(c.getID());
              saveContentsForTikerNews(c);
              break;

            case 3:
              deleteOldContentsForTikerNews(c.getID());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForTikerNews(TikerNews_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getID());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());

      long rid = db.insert(Constants.TIKERNEWS_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForTikerNews(String Id) {

    try {
      String sql = "DELETE FROM " + Constants.TIKERNEWS_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForTikerNews() {

    try {
      String sql = "DELETE FROM " + Constants.TIKERNEWS_TABLE;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void ContentsForNews(String strChangeDate) {
    try {
      ArrayList<News_Entity> contents = null;
      contents = downloadRecentContentsForNews(strChangeDate);
      if (contents != null && contents.size() > 0) {
        for (News_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.NEWS_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForNews(c);
              break;

            case 2:
              deleteOldContentsForNews(c.getId());
              saveContentsForNews(c);
              break;

            case 3:
              deleteOldContentsForNews(c.getId());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForNews(News_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getSectionID() != null && !c.getSectionID().equals(""))
        vals.put(Constants.SECTIONID, c.getSectionID());
      if (c.getStatus() != 0) vals.put(Constants.STATUS, c.getStatus());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getText() != null && !c.getText().equals("")) vals.put(Constants.TEXT, c.getText());
      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());
      if (c.getStartDate() != null && !c.getStartDate().equals(""))
        vals.put(Constants.STARTDATE, c.getStartDate());
      if (c.getEndDate() != null && !c.getEndDate().equals(""))
        vals.put(Constants.ENDDATE, c.getEndDate());
      if (c.getIsFirst() != 0) vals.put(Constants.ISFIRST, c.getIsFirst());
      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      vals.put(Constants.VIEWD, 0);
      long rid = db.insert(Constants.NEWS_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void UpdateOldContentsForNews(String Id) {

    try {
      String sql =
          "Update "
              + Constants.NEWS_TABLE
              + " Set Deleted=1 , Viewd=1 "
              + " WHERE "
              + Constants.ID
              + "="
              + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForNews(String Id) {

    try {
      String sql = "Delete From " + Constants.NEWS_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void UpdateSectionedNews(String Id) {

    try {
      String sql =
          "Update "
              + Constants.NEWS_TABLE
              + " Set Deleted=1 , Viewd=1"
              + " WHERE "
              + Constants.SECTIONID
              + "="
              + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForNews() {

    try {
      String sql = "Update " + Constants.NEWS_TABLE + " Set Deleted=1 , Viewd=1";
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForNews(int iSectionID) {

    try {
      String sql =
          "Update "
              + Constants.NEWS_TABLE
              + " Set Deleted=1 , Viewd=1  WHERE "
              + Constants.SECTIONID
              + "="
              + iSectionID;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private ArrayList<News_Entity> downloadRecentContentsForNews(String strChangeDate)
      throws IOException, ParserConfigurationException, SAXException {
    return new ContentParser(context.getString(R.string.news_url) + "" + strChangeDate).getNews();
  }

  public int NewsUnReadCount(String strsection) {
    int iCount = 0;
    try {
      String sql =
          "Select  COUNT(*) FROM "
              + Constants.NEWS_TABLE
              + " Where Viewd=0 AND SectionID= "
              + strsection;
      Cursor crsr = db.rawQuery(sql, null);
      crsr.moveToNext();
      iCount = crsr.getInt(0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return iCount;
  }

  public synchronized void ContentsForAds(String strChangeDate) {
    try {
      ArrayList<Ads_Entity> contents = null;
      contents = downloadRecentContentsForAds(strChangeDate);
      if (contents != null && contents.size() > 0) {
        if (strChangeDate.equals("0")) deletleOldContentsForAds();
        for (Ads_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.ADS_TABLE);
          switch (Integer.parseInt(c.getLastChangeType())) {
            case 1:
              saveContentsForAds(c);
              break;

            case 2:
              deleteOldContentsForAds(c.getId());
              saveContentsForAds(c);
              break;

            case 3:
              deleteOldContentsForAds(c.getId());
              break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void ContentsForAdsByID(String strID) {
    try {
      ArrayList<Ads_Entity> contents = null;
      contents = downloadRecentContentsForAdsByID(strID);
      if (contents != null && contents.size() > 0) {
        for (Ads_Entity c : contents) {
          saveContentsForAds(c);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForAds(Ads_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getSectionID() != null && !c.getSectionID().equals(""))
        vals.put(Constants.SECTIONID, c.getSectionID());

      if (c.getStatus() != 0) vals.put(Constants.STATUS, c.getStatus());
      if (c.getIsFirst() != 0) vals.put(Constants.ISFIRST, c.getIsFirst());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getText() != null && !c.getText().equals("")) vals.put(Constants.TEXT, c.getText());
      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());
      if (c.getStartDate() != null && !c.getStartDate().equals(""))
        vals.put(Constants.STARTDATE, c.getStartDate());
      if (c.getEndDate() != null && !c.getEndDate().equals(""))
        vals.put(Constants.ENDDATE, c.getEndDate());
      if (c.getEndDateString() != null && !c.getEndDateString().equals(""))
        vals.put(Constants.ENDDATESTRING, c.getEndDateString());

      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      vals.put(Constants.VIEWD, 0);
      long rid = db.insert(Constants.ADS_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void UpdateOldContentsForAds(String Id) {

    try {
      String sql =
          "Update "
              + Constants.ADS_TABLE
              + " Set Deleted=1 , Viewd=1 WHERE "
              + Constants.ID
              + "="
              + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForAds(String Id) {

    try {
      String sql = "Delete From " + Constants.ADS_TABLE + " WHERE " + Constants.ID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deletleOldContentsForAds() {

    try {
      String sql = "Update  " + Constants.ADS_TABLE + "Set Views=1";
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void updateSectionedAds(String Id) {

    try {
      String sql =
          "Update "
              + Constants.ADS_TABLE
              + " Set Deleted=1 , Viewd=1 WHERE "
              + Constants.SECTIONID
              + "="
              + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteSectionedAds(String Id) {

    try {
      String sql =
          "Delete From " + Constants.ADS_TABLE + " WHERE " + Constants.SECTIONID + "=" + Id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public synchronized void deleteOldContentsForAds(int iSectionID) {

    try {
      String sql =
          "Update "
              + Constants.ADS_TABLE
              + "  Set Deleted=1 , Viewd=1 WHERE "
              + Constants.SECTIONID
              + "="
              + iSectionID;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private ArrayList<Ads_Entity> downloadRecentContentsForAds(String strChangeDate)
      throws IOException, ParserConfigurationException, SAXException {
    return new ContentParser(context.getString(R.string.ads_url) + "" + strChangeDate).getAds();
  }

  private ArrayList<Ads_Entity> downloadRecentContentsForAdsByID(String StrID)
      throws IOException, ParserConfigurationException, SAXException {
    return new ContentParser(context.getString(R.string.ads_detail_url) + "" + StrID).getAds();
  }

  public int AdsUnReadCount(String strsection) {
    int iCount = 0;
    try {
      String sql =
          "Select  COUNT(*) FROM "
              + Constants.ADS_TABLE
              + " Where Viewd=0 AND SectionID= "
              + strsection;
      Cursor crsr = db.rawQuery(sql, null);
      crsr.moveToNext();
      iCount = crsr.getInt(0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return iCount;
  }
  ////////////////////////////////////////////////////////////////////////////
  public int IsFavorite_News(String Id) {
    Cursor cur =
        db.rawQuery(
            "SELECT COUNT(*) FROM  "
                + Constants.NEWS_FAVORITE_TABLE
                + " WHERE "
                + Constants.ID
                + " = "
                + Id,
            null);
    cur.moveToFirst();
    int jcount = cur.getInt(0);
    cur.close();
    return jcount;
  }

  /////////////////////////////////////////////////////////////
  public synchronized void deleteFavoriteNews(String id, String section_id) {
    try {
      String sql =
          "DELETE FROM "
              + Constants.NEWS_FAVORITE_TABLE
              + " WHERE "
              + Constants.ID
              + "="
              + id
              + " AND "
              + Constants.SECTIONID
              + "="
              + section_id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.getMessage();
    }
  }

  /////////////////////////////////////////////////////////////
  public synchronized void deleteFavoriteNews(String id) {
    try {
      String sql =
          "DELETE FROM " + Constants.NEWS_FAVORITE_TABLE + " WHERE " + Constants.ID + "=" + id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.getMessage();
    }
  }
  ////////////////////////////////////////////////////////////////
  public void saveContentsForFavorite_News(News_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getSectionID() != null && !c.getSectionID().equals(""))
        vals.put(Constants.SECTIONID, c.getSectionID());
      if (c.getStatus() != 0) vals.put(Constants.STATUS, c.getStatus());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getText() != null && !c.getText().equals("")) vals.put(Constants.TEXT, c.getText());
      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());
      if (c.getStartDate() != null && !c.getStartDate().equals(""))
        vals.put(Constants.STARTDATE, c.getStartDate());
      if (c.getEndDate() != null && !c.getEndDate().equals(""))
        vals.put(Constants.ENDDATE, c.getEndDate());

      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      vals.put(Constants.VIEWD, 0);
      long rid = db.insert(Constants.NEWS_FAVORITE_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public void updateNumViewd(String Id, String Type, String NumViewd) {
    try {
      String sql =
          "DELETE FROM "
              + Constants.NUMVIED_TABLE
              + " WHERE "
              + Constants.ID
              + "="
              + Id
              + " AND "
              + Constants.TYPE
              + " = "
              + Type;
      db.execSQL(sql);
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, Id);
      vals.put(Constants.TYPE, Type);
      vals.put(Constants.NUMVIEWD, NumViewd);
      long rid = db.insert(Constants.NUMVIED_TABLE, null, vals);

      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to update content ";
      }

    } catch (Exception ex) {
    }
  }

  public synchronized String selectNumViews(String Id, String Type) {
    String strNumViews = "";

    try {

      Cursor cursor =
          db.rawQuery(
              "select "
                  + Constants.NUMVIEWD
                  + " from "
                  + Constants.NUMVIED_TABLE
                  + " where "
                  + Constants.ID
                  + "="
                  + Id
                  + " AND "
                  + Constants.TYPE
                  + "="
                  + Type,
              null);
      if (cursor == null) return strNumViews;

      try {
        if (cursor.moveToFirst()) strNumViews = cursor.getString(0);
      } finally {
        cursor.close();
      }
      return strNumViews;
    } catch (SQLException e) {
      Log.e(Constants.TAG, "An exception was thrown", e);
      return strNumViews;
    }
  }

  //////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////
  public int IsFavorite_Ads(String Id) {
    Cursor cur =
        db.rawQuery(
            "SELECT COUNT(*) FROM  "
                + Constants.ADS_FAVORITE_TABLE
                + " WHERE "
                + Constants.ID
                + " = "
                + Id,
            null);
    cur.moveToFirst();
    int jcount = cur.getInt(0);
    cur.close();
    return jcount;
  }

  /////////////////////////////////////////////////////////////
  public synchronized void deleteFavoriteAds(String id, String section_id) {
    try {
      String sql =
          "DELETE FROM "
              + Constants.ADS_FAVORITE_TABLE
              + " WHERE "
              + Constants.ID
              + "="
              + id
              + " AND "
              + Constants.SECTIONID
              + "="
              + section_id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.getMessage();
    }
  }

  /////////////////////////////////////////////////////////////
  public synchronized void deleteFavoriteAds(String id) {
    try {
      String sql =
          "DELETE FROM " + Constants.ADS_FAVORITE_TABLE + " WHERE " + Constants.ID + "=" + id;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.getMessage();
    }
  }

  ////////////////////////////////////////////////////////////////
  public void saveContentsForFavorite_Ads(Ads_Entity c) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getId());
      if (c.getSectionID() != null && !c.getSectionID().equals(""))
        vals.put(Constants.SECTIONID, c.getSectionID());
      if (c.getStatus() != 0) vals.put(Constants.STATUS, c.getStatus());
      if (c.getTitle() != null && !c.getTitle().equals("")) vals.put(Constants.TITLE, c.getTitle());
      if (c.getText() != null && !c.getText().equals("")) vals.put(Constants.TEXT, c.getText());
      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());
      if (c.getStartDate() != null && !c.getStartDate().equals(""))
        vals.put(Constants.STARTDATE, c.getStartDate());
      if (c.getEndDate() != null && !c.getEndDate().equals(""))
        vals.put(Constants.ENDDATE, c.getEndDate());

      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());
      vals.put(Constants.VIEWD, 0);
      long rid = db.insert(Constants.ADS_FAVORITE_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  ////////////////////////////////////////
  public boolean updateNewsViewedStatus(String section_id, int newStatus) {
    boolean result = false;
    try {
      String sql =
          String.format(
              Locale.US,
              "UPDATE %s SET %s = %s WHERE (%s = %s) ",
              Constants.NEWS_TABLE,
              Constants.VIEWD,
              newStatus,
              Constants.SECTIONID,
              section_id);
      db.execSQL(sql);
      result = true;
    } catch (Exception ex) {
      ex.getMessage();
    }
    return result;
  }
  //////////////////////////////////////////////////////////////////
  public boolean updateAdsViewedStatus(String section_id, int newStatus) {
    boolean result = false;
    try {
      String sql =
          String.format(
              Locale.US,
              "UPDATE %s SET %s = %s WHERE (%s = %s) ",
              Constants.ADS_TABLE,
              Constants.VIEWD,
              newStatus,
              Constants.SECTIONID,
              section_id);
      db.execSQL(sql);
      result = true;
    } catch (Exception ex) {
      ex.getMessage();
    }
    return result;
  }

  public synchronized void ContentsForSponsor(ArrayList<Sponsor_Entity> contents, String strViews) {
    try {

      if (contents != null && contents.size() > 0) {
        deleteOldContentsForSponsor();
        for (Sponsor_Entity c : contents) {
          if (contents.indexOf(c) == 0)
            updateLatestClientUpdate(c.getLastChange(), Constants.SPONSOR_TABLE);
          saveContentsForSponsor(c, strViews);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void saveContentsForSponsor(Sponsor_Entity c, String strViews) {
    try {
      ContentValues vals = new ContentValues();
      vals.put(Constants.ID, c.getID());

      if (c.getName() != null && !c.getName().equals("")) vals.put(Constants.NAME, c.getName());

      if (c.getImage() != null && !c.getImage().equals("")) vals.put(Constants.IMAGE, c.getImage());

      if (c.getDetails() != null && !c.getDetails().equals(""))
        vals.put(Constants.DETAILS, c.getDetails());

      if (c.getEmail() != null && !c.getEmail().equals("")) vals.put(Constants.EMAIL, c.getEmail());

      if (c.getTel() != null && !c.getTel().equals("")) vals.put(Constants.PHONE, c.getTel());

      if (c.getWebsite() != null && !c.getWebsite().equals(""))
        vals.put(Constants.WEBSITE, c.getWebsite());

      if (c.getLastChange() != null && !c.getLastChange().equals(""))
        vals.put(Constants.LASTCHANGE, c.getLastChange());

      if (strViews != null && !strViews.equals("")) vals.put(Constants.NOOFVIEWS, strViews);

      long rid = db.insert(Constants.SPONSOR_TABLE, null, vals);
      if (rid == -1) {
        String err = "ContentUpdater.saveContents: Unable to insert content: " + c.toString();
      }

    } catch (Exception ex) {
    }
  }

  public synchronized void deleteOldContentsForSponsor() {

    try {
      String sql = "DELETE FROM " + Constants.SPONSOR_TABLE;
      db.execSQL(sql);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  //////////////////////////////////////////////////////////////////
  public boolean updateSponsorNoView(String strViews) {
    boolean result = false;
    try {
      String sql =
          String.format(
              Locale.US,
              "UPDATE %s SET %s = %s  ",
              Constants.SPONSOR_TABLE,
              Constants.NOOFVIEWS,
              strViews);
      db.execSQL(sql);
      result = true;
    } catch (Exception ex) {
      ex.getMessage();
    }
    return result;
  }
}
