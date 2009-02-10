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
package sdloader.javaee.webxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.Assertion;
import sdloader.util.CollectionsUtil;

/**
 * web.xmlファイルからWebXmlを構築します。
 * 
 * @author c9katayama
 */
public class WebXmlBuilder {

	private static SDLoaderLog log = SDLoaderLogFactory
			.getLog(WebXmlBuilder.class);

	private static Map<String, String> registrations = CollectionsUtil
			.newHashMap();
	static {
		registrations.put(
				"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN",
				"/sdloader/resource/web-app_2_2.dtd");
		registrations.put(
				"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
				"/sdloader/resource/web-app_2_3.dtd");
	}

	public WebXml build(final URL webXmlUrl) throws IOException, SAXException,
			ParserConfigurationException {
		log.info("load start web.xml. Path=" + webXmlUrl);
		InputStream is = webXmlUrl.openStream();
		WebXml webxml = createWebXml(is);
		log.info("load success web.xml. Path=" + webXmlUrl);
		return webxml;
	}

	private WebXml createWebXml(InputStream is) throws IOException,
			SAXException, ParserConfigurationException {

		final SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		final WebXmlParseHandler handler = new WebXmlParseHandler();
		for (Iterator<String> itr = registrations.keySet().iterator(); itr
				.hasNext();) {
			String key = itr.next();
			String value = registrations.get(key);
			URL url = WebXml.class.getResource(value);
			if (url != null) {
				handler
						.register(Assertion.notNull(key), Assertion
								.notNull(url));
			} else {
				log.warn("registration resource not found.key=" + key
						+ " value=" + value);
			}
		}
		sax.parse(is, handler);
		WebXml webxml = new WebXml();
		WebAppTag webAppTag = (WebAppTag) handler.getRootObject();
		webxml.setWebApp(webAppTag);
		return webxml;
	}

}
