/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sdloader.j2ee.webxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.SAXException;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;

/**
 * web.xmlを読み込み、WebXmlのインスタンスを生成します。
 * 
 * @author c9katayama
 */
public class WebXmlFactory {

	private static final SDLoaderLog log = SDLoaderLogFactory.getLog(WebXmlFactory.class);
	
	private static Map registrations = new HashMap();
	static{
		registrations.put("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN","/sdloader/resource/web-app_2_2.dtd");
		registrations.put("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN","/sdloader/resource/web-app_2_3.dtd");
	}
	
	public static WebXml createWebXml(InputStream is) throws IOException,
			SAXException, ParserConfigurationException {

		SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		WebXmlParseHandler handler = new WebXmlParseHandler();
		
		for(Iterator itr = registrations.keySet().iterator();itr.hasNext();){
			String key = (String)itr.next();
			String value = (String)registrations.get(key);
			URL url = WebXml.class.getResource(value);
			if (url != null) {
				handler.register(key,url);
			}else{
				log.warn("registration resource not found.key="+key+" value="+value);
			}
		}
		sax.parse(is, handler);
		WebXml webxml = new WebXml();
		webxml.setWebApp((WebAppTag) handler.getRootObject());
		return webxml;
	}
}
