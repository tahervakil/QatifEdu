package com.taher.qatifedu.handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.Ads_Entity;

import java.util.ArrayList;

public class ContentHandler_Ads extends DefaultHandler {
	private final static String ID = "ID";
	private final static String SectionID = "SectionID";
	private final static String Status = "Status";
	private final static String Title = "Title";
	private final static String Text = "Text";
	private final static String Image = "Image";
	private final static String Views = "Views";
	private final static String StartDate = "StartDate";
	private final static String EndDate = "EndDate";
	private final static String EndDateNew = "EndDateNew";
	private final static String LastChange = "LastChange";
	private final static String LastChangeType = "LastChangeType";
	private final static String IsFirst = "IsFirst";
	private final static String record = "record";
	

	private ArrayList<Ads_Entity> contents;
	private StringBuilder sb;
	private Ads_Entity currentContent;

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
				currentContent.setId(sb.toString());
			}if (localName.equalsIgnoreCase(SectionID)) {
				currentContent.setSectionID(sb.toString());
			}if (localName.equalsIgnoreCase(Status)) {
				currentContent.setStatus(Integer.parseInt(sb.toString()));
			}if (localName.equalsIgnoreCase(Title)) {
				currentContent.setTitle(sb.toString().trim());
			}if (localName.equalsIgnoreCase(Text)) {
				currentContent.setText(sb.toString());
			}if (localName.equalsIgnoreCase(Image)) {
				currentContent.setImage(sb.toString());
			}if (localName.equalsIgnoreCase(Views)) {
				currentContent.setViews(sb.toString());
			}if (localName.equalsIgnoreCase(StartDate)) {
				currentContent.setStartDate(sb.toString());
			}if (localName.equalsIgnoreCase(EndDate)) {
				currentContent.setEndDate(sb.toString());
			}if (localName.equalsIgnoreCase(EndDateNew)) {
				currentContent.setEndDateString(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChange)) {
				currentContent.setLastChange(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChangeType)) {
				currentContent.setLastChangeType(sb.toString());
			}else if (localName.equalsIgnoreCase(IsFirst)) {
				currentContent.setIsFirst(Integer.parseInt(sb.toString()));
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
		contents = new ArrayList<Ads_Entity>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(record)) {
			currentContent = new Ads_Entity();
		}
	}

	public ArrayList<Ads_Entity> getContents() {
		return contents;
	}
}
