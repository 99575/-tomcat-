package com.yc.tomcat.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ParseXml {

	private static Map<String, String> map = new HashMap<String, String>();
	private static ParseXml parseXml = new ParseXml();
	
	private ParseXml() {
		SAXReader read = new SAXReader();
		Document doc = null;

		try {
			doc = read.read(this.getClass().getClassLoader().getResourceAsStream("web.xml"));
			List<Element> mimes = doc.selectNodes("//mime-mapping");

			// 循环解析
			for (Element e : mimes) {
				map.put(e.selectSingleNode("extension").getText().trim(),
						e.selectSingleNode("mime-type").getText().trim());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
//	public static ParseXml getParseXml(){
//		return parseXml;
//	}


	public static String getContentType(String key) {
		return map.getOrDefault(key, "text/html;charset=utf-8");
	}
}
