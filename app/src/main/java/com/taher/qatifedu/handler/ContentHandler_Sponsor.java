package com.taher.qatifedu.handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.Sponsor_Entity;

import java.util.ArrayList;

public class ContentHandler_Sponsor extends DefaultHandler {
	private final static String ID = "ID";
	private final static String NAME = "name";
	private final static String IMAGE = "image_file";
	private final static String DETAILS = "details";
	private final static String Phone = "tel";
    private final static String Email = "Email";
    private final static String Website = "Website";
	private final static String LastChange = "LastChange";
	private final static String LastChangeType = "LastChangeType";
	private final static String NoOfViews = "NoOfViews";
	private final static String record = "record";
	

	private ArrayList<Sponsor_Entity> contents;
	private StringBuilder sb;
	private Sponsor_Entity currentContent;

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
			} else if (localName.equalsIgnoreCase(NAME)) {
				currentContent.setName(sb.toString());
			}else if (localName.equalsIgnoreCase(IMAGE)) {
				currentContent.setImage(sb.toString());
			} else if (localName.equalsIgnoreCase(DETAILS)) {
				currentContent.setDetails(sb.toString());
			} else if (localName.equalsIgnoreCase(Phone)) {
				currentContent.setTel(sb.toString());
			}else if (localName.equalsIgnoreCase(Email)) {
				currentContent.setEmail(sb.toString());
			}else if (localName.equalsIgnoreCase(Website)) {
				currentContent.setWebsite(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChange)) {
				currentContent.setLastChange(sb.toString());
			}else if (localName.equalsIgnoreCase(LastChangeType)) {
				currentContent.setLastChangeType(sb.toString());
			}else if (localName.equalsIgnoreCase(NoOfViews)) {
				currentContent.setNoOfViews(sb.toString());
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
		contents = new ArrayList<Sponsor_Entity>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(record)) {
			currentContent = new Sponsor_Entity();
		}
	}

	public ArrayList<Sponsor_Entity> getContents() {
		return contents;
	}
}
