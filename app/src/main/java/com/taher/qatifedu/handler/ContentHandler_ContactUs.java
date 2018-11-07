package com.taher.qatifedu.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.taher.qatifedu.entity.ContactUs_Entity;


public class ContentHandler_ContactUs extends DefaultHandler {
	private final static String phone = "phone";
	private final static String email = "email";
	private final static String facebook = "facebook";
	private final static String twitter = "twitter";
	private final static String instagram = "instagram";
	private final static String record = "record";
	private final static String website = "website";
	private final static String youtube = "youtube";
	private final static String snapshat = "snapchat";
	private final static String telegram = "telegram";
	private final static String whatsapp = "whatsapp";
	private ContactUs_Entity contents;
	private StringBuilder sb;
	private ContactUs_Entity currentContactUsContent;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (currentContactUsContent != null) {
			if (localName.equalsIgnoreCase(phone)) {
				currentContactUsContent.setPhone(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(email)) {
				currentContactUsContent.setEmail(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(facebook)) {
				currentContactUsContent.setFacebook(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(twitter)) {
				currentContactUsContent.setTwitter(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(instagram)) {
				currentContactUsContent.setInstegram(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(website)) {
				currentContactUsContent.setWebsite(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(youtube)) {
				currentContactUsContent.setYoutube(sb.toString().replaceAll("\n", "").trim());
			}  else if (localName.equalsIgnoreCase(snapshat)) {
				currentContactUsContent.setSnapChat(sb.toString().replaceAll("\n", "").trim());
			}  else if (localName.equalsIgnoreCase(telegram)) {
				currentContactUsContent.setTelegram(sb.toString().replaceAll("\n", "").trim());
			} else if (localName.equalsIgnoreCase(whatsapp)) {
				currentContactUsContent.setWhatsapp(sb.toString().replaceAll("\n", "").trim());
			}
			else if (localName.equalsIgnoreCase(record)) {
				contents = currentContactUsContent;
			}
			sb.setLength(0);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		sb = new StringBuilder();
		contents = new ContactUs_Entity();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (localName.equalsIgnoreCase(record)) {
			currentContactUsContent = new ContactUs_Entity();
		}
	}

	public ContactUs_Entity getContents() {
		return contents;
	}
}
