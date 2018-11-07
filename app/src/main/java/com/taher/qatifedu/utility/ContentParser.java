package com.taher.qatifedu.utility;
import org.xml.sax.SAXException;
import com.taher.qatifedu.entity.Ads_Entity;
import com.taher.qatifedu.entity.Banner_Entity;
import com.taher.qatifedu.entity.Company_Entity;
import com.taher.qatifedu.entity.ContactUs_Entity;
import com.taher.qatifedu.entity.More_Entity;
import com.taher.qatifedu.entity.News_Entity;
import com.taher.qatifedu.entity.Section_Entity;
import com.taher.qatifedu.entity.Sponsor_Entity;
import com.taher.qatifedu.entity.TikerNews_Entity;
import com.taher.qatifedu.handler.ContentHandler_Ads;
import com.taher.qatifedu.handler.ContentHandler_Banners;
import com.taher.qatifedu.handler.ContentHandler_Company;
import com.taher.qatifedu.handler.ContentHandler_ContactUs;
import com.taher.qatifedu.handler.ContentHandler_General;
import com.taher.qatifedu.handler.ContentHandler_News;
import com.taher.qatifedu.handler.ContentHandler_Section;
import com.taher.qatifedu.handler.ContentHandler_Sponsor;
import com.taher.qatifedu.handler.ContentHandler_TikerNews;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ContentParser {
	private String urlString;
	private HttpURLConnection conn;

	public ContentParser(String urlString) {
		this.urlString = urlString;
	}

	public InputStream load(String urlString, int timeout, String method) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeout);
		InputStream istream = conn.getInputStream();
		return istream;
	}
	
	

	
	
	public ArrayList<More_Entity> getGeneralContents() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<More_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_General handler = new ContentHandler_General();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
///////////////////////////////////////////////////////////////////////////////
	
	public ContactUs_Entity getContactUsContents() throws IOException, ParserConfigurationException, SAXException {
		ContactUs_Entity contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_ContactUs handler = new ContentHandler_ContactUs();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
///////////////////////////////////////////////////////////////////////////////
	
	
	public ArrayList<Section_Entity> getSectionContents() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<Section_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Section handler = new ContentHandler_Section();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
///////////////////////////////////////////////////////////////////////////////
	
	
	public ArrayList<Company_Entity> getCompaniesContents() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<Company_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Company handler = new ContentHandler_Company();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
	////////////////////////////////////////////////////////////////////////
	
	public ArrayList<Banner_Entity> getBanners() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<Banner_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Banners handler = new ContentHandler_Banners();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////
	
	public ArrayList<TikerNews_Entity> getTikerNews() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<TikerNews_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_TikerNews handler = new ContentHandler_TikerNews();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
////////////////////////////////////////////////////////////////////////
	
	public ArrayList<News_Entity> getNews() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<News_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_News handler = new ContentHandler_News();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
	
////////////////////////////////////////////////////////////////////////
	
	public ArrayList<Ads_Entity> getAds() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<Ads_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Ads handler = new ContentHandler_Ads();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
	
	public ArrayList<Sponsor_Entity> getSponsor() throws IOException, ParserConfigurationException, SAXException {
		ArrayList<Sponsor_Entity> contents = null;
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Sponsor handler = new ContentHandler_Sponsor();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
			contents = handler.getContents();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return contents;
	}
	
	public void getSponsorView() throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory fac = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			ContentHandler_Sponsor handler = new ContentHandler_Sponsor();
			parser = fac.newSAXParser();
			InputStream istream = load(urlString, 60000, "GET");
			parser.parse(istream, handler);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
}
