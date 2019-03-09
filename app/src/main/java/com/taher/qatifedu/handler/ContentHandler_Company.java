package com.taher.qatifedu.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.Company_Entity;

import java.util.ArrayList;

public class ContentHandler_Company extends DefaultHandler {
  private static final String ID = "ID";
  private static final String Name = "Title";
  private static final String Logo = "Image";
  private static final String LastChange = "LastChange";
  private static final String LastChangeType = "LastChangeType";
  private static final String record = "record";
  private static final String parentId = "ParentID";
  private static final String subCategoryType = "type";
  private ArrayList<Company_Entity> contents;
  private StringBuilder sb;
  private Company_Entity companyEntity;

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    super.characters(ch, start, length);
    sb.append(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    super.endElement(uri, localName, name);
    if (companyEntity != null) {
      if (localName.equalsIgnoreCase(ID)) {
        companyEntity.setId(sb.toString());
      } else if (localName.equalsIgnoreCase(Name)) {
        companyEntity.setName(sb.toString());
      } else if (localName.equalsIgnoreCase(Logo)) {
        companyEntity.setLogo(sb.toString());
      } else if (localName.equalsIgnoreCase(LastChange)) {
        companyEntity.setLastChange(sb.toString());
      } else if (localName.equalsIgnoreCase(LastChangeType)) {
        companyEntity.setLastChangeType(sb.toString());
      } else if (localName.equalsIgnoreCase(parentId)) {
        companyEntity.setParentId(Integer.parseInt(sb.toString()));
      } else if (localName.equalsIgnoreCase(subCategoryType)) {
        companyEntity.setSubCategoryType(Integer.parseInt(sb.toString()));
      } else if (localName.equalsIgnoreCase(record)) {
        contents.add(companyEntity);
      }
      sb.setLength(0);
    }
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    sb = new StringBuilder();
    contents = new ArrayList<>();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException {
    super.startElement(uri, localName, name, attributes);

    if (localName.equalsIgnoreCase(record)) {
      companyEntity = new Company_Entity();
    }
  }

  public ArrayList<Company_Entity> getContents() {
    return contents;
  }
}
