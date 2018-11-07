package com.taher.qatifedu.handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.TikerNews_Entity;

import java.util.ArrayList;

public class ContentHandler_TikerNews extends DefaultHandler {
	private final static String ID = "ID";
	private final static String Title = "Title";
	private final static String LastChange = "LastChange";
	private final static String LastChangeType = "LastChangeType";
	private final static String record = "record";
	

	private ArrayList<TikerNews_Entity> contents;
	private StringBuilder sb;
	private TikerNews_Entity currentContent;

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
		contents = new ArrayList<TikerNews_Entity>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(record)) {
			currentContent = new TikerNews_Entity();
		}
	}

	public ArrayList<TikerNews_Entity> getContents() {
		return contents;
	}
}
