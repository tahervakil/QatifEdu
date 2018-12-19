package com.taher.qatifedu.handler;

import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.entity.Event_Entity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContentHandler_Event extends DefaultHandler {

    private static final String ID = "ID";
    public static final String title = "Title";
    public static final String details = "Details";
    private static final String record = "record";

    private ArrayList<Event_Entity> contents;
    private StringBuilder sb;
    private Event_Entity currentContent;

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
            }
            if (localName.equalsIgnoreCase(title)) {
                currentContent.setTitle(sb.toString().trim());
            }
            if (localName.equalsIgnoreCase(details)) {
                currentContent.setDetails(sb.toString().trim());
            } else if (localName.equalsIgnoreCase(record)) {
                contents.add(currentContent);
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
            currentContent = new Event_Entity();
        }
    }

    public ArrayList<Event_Entity> getContents() {
        return contents;
    }
}
