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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import sdloader.j2ee.webxml.WebAppTag;
import sdloader.j2ee.webxml.WebXmlParseHandler;

import junit.framework.TestCase;

public class WebXmlParseHandlerTest extends TestCase {

	public void testWebXmlParseHandler() throws Exception{		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		WebXmlParseHandler handler = new WebXmlParseHandler();
		parser.parse(WebXmlParseHandlerTest.class.getResourceAsStream("web.xml"),handler);
		
		WebAppTag webapp = (WebAppTag)handler.getRootObject();
		
		
	}
}
