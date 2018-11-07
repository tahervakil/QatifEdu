package com.taher.qatifedu.handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.Banner_Entity;

import java.util.ArrayList;

public class ContentHandler_Banners extends DefaultHandler {
	private final static String ID = "ID";
	private final static String Title = "Title";
	private final static String Details = "Details";
	private final static String IMAGE = "FileName";
	private final static String Start_Date = "StartDate";
	private final static String Expiry_Date = "EndDate";
	private final static String LastChange = "LastChange";
	private final static String LastChangeType = "LastChangeType";
	private final static String record = "record";
	

	private ArrayList<Banner_Entity> contents;
	private StringBuilder sb;
	private Banner_Entity currentContent;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (currentContent != null) {
			if (localName.equalsIgnoreCase(ID)) {
				currentContent.setID(sb.toString());
			}if (localName.equalsIgnoreCase(Title)) {
				currentContent.setTitle(sb.toString());
			}if (localName.equalsIgnoreCase(Details)) {
				currentContent.setDetials(sb.toString());
			} else if (localName.equalsIgnoreCase(IMAGE)) {
				currentContent.setImage(sb.toString());
			} else if (localName.equalsIgnoreCase(Start_Date)) {
				currentContent.setStart_Date(sb.toString());
			}else if (localName.equalsIgnoreCase(Expiry_Date)) {
				currentContent.setExpiry_Date(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChange)) {
				currentContent.setLastChange(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChangeType)) {
				currentContent.setLastChangeType(sb.toString());
			}else if (localName.equalsIgnoreCase(record)) {
				contents.add(currentContent);
			}
			sb.setLength(0);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		sb = new StringBuilder();
		contents = new ArrayList<Banner_Entity>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(record)) {
			currentContent = new Banner_Entity();
		}
	}

	public ArrayList<Banner_Entity> getContents() {
		return contents;
	}
}
