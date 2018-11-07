package com.taher.qatifedu.handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.More_Entity;

import java.util.ArrayList;

public class ContentHandler_General extends DefaultHandler {
    private final static String ID = "ID";
    private final static String PageName = "PageName";
    private final static String PageContents = "PageContents";
    private final static String LastChange  = "LastChange";
	private final static String LastChangeType = "LastChangeType";
   
    private final static String record = "record";

    private ArrayList<More_Entity> contents;
    private StringBuilder sb;
    private More_Entity currentContent;

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
            } else if (localName.equalsIgnoreCase(PageName)) {
                currentContent.setName(sb.toString());
            } else if (localName.equalsIgnoreCase(PageContents)) {
                currentContent.setContent(sb.toString());
            }  else if (localName.equalsIgnoreCase(LastChange)) {
                currentContent.setLastChange(sb.toString());
            } else if (localName.equalsIgnoreCase(LastChangeType)) {
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
        contents = new ArrayList<More_Entity>();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);

        if (localName.equalsIgnoreCase(record)) {
            currentContent = new More_Entity();
        }
    }

    public ArrayList<More_Entity> getContents() {
        return contents;
    }
}
