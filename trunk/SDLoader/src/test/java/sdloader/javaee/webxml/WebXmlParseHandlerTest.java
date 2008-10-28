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

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

public class WebXmlParseHandlerTest extends TestCase {

	public void testWebXmlParseHandler() throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		WebXmlParseHandler handler = new WebXmlParseHandler();
		handler.register(
				"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
				WebXmlParseHandler.class
						.getResource("/sdloader/resource/web-app_2_3.dtd"));
		parser.parse(WebXmlParseHandlerTest.class
				.getResourceAsStream("web.xml"), handler);

		WebAppTag webAppTag = (WebAppTag) handler.getRootObject();

		assertContextParamTag(webAppTag);
		assertFilterTag(webAppTag);
		assertWelcomeFileListTag(webAppTag);

	}

	private void assertContextParamTag(WebAppTag webAppTag) {
		List<ContextParamTag> contextParamTagList = webAppTag.getContextParam();
		assertEquals(2, contextParamTagList.size());
		ContextParamTag contextParamTag1 = (ContextParamTag) contextParamTagList
				.get(0);
		assertEquals("CONTEXT-PARAM-NAME1", contextParamTag1.getParamName());
		assertEquals("CONTEXT-PARAM-VALUE1", contextParamTag1.getParamValue());
		assertEquals("CONTEXT-PARAM-DESC11CONTEXT-PARAM-DESC12",
				contextParamTag1.getDescription());

		ContextParamTag contextParamTag2 = (ContextParamTag) contextParamTagList
				.get(1);
		assertEquals("CONTEXT-PARAM-NAME2", contextParamTag2.getParamName());
		assertEquals("CONTEXT-PARAM-VALUE2", contextParamTag2.getParamValue());
		assertEquals("CONTEXT-PARAM-DESC21CONTEXT-PARAM-DESC22",
				contextParamTag2.getDescription());
	}

	private void assertFilterTag(WebAppTag webAppTag) {
		List<FilterTag> filterTagList = webAppTag.getFilter();
		assertEquals(2, filterTagList.size());
		FilterTag filter1 = (FilterTag) filterTagList.get(0);
		assertEquals("FILTER-NAME1", filter1.getFilterName());
		assertEquals("FILTER-CLASS1", filter1.getFilterClass());
		InitParamTag initParamTag1 = (InitParamTag) filter1.getInitParamList()
				.get(0);
		assertEquals("FILTER-PARAM-NAME1", initParamTag1.getParamName());
		assertEquals("FILTER-PARAM-VALUE1", initParamTag1.getParamValue());
		InitParamTag initParamTag11 = (InitParamTag) filter1.getInitParamList()
				.get(1);
		assertEquals("FILTER-PARAM-NAME11", initParamTag11.getParamName());
		assertEquals("FILTER-PARAM-VALUE11", initParamTag11.getParamValue());
		assertEquals("FILTER-PARAM-VALUE1", filter1
				.getInitParam("FILTER-PARAM-NAME1"));
		assertEquals("FILTER-PARAM-VALUE11", filter1
				.getInitParam("FILTER-PARAM-NAME11"));

		FilterTag filter2 = (FilterTag) filterTagList.get(1);
		assertEquals("FILTER-NAME2", filter2.getFilterName());
		assertEquals("FILTER-CLASS2", filter2.getFilterClass());
		InitParamTag initParamTag2 = (InitParamTag) filter2.getInitParamList()
				.get(0);
		assertEquals("FILTER-PARAM-NAME2", initParamTag2.getParamName());
		assertEquals("FILTER-PARAM-VALUE2", initParamTag2.getParamValue());
	}

	private void assertWelcomeFileListTag(WebAppTag webAppTag) {
		WelcomeFileListTag welcomeFileListTag = webAppTag.getWelcomeFileList();
		List<String> welcomeList = welcomeFileListTag.getWelcomeFile();
		assertEquals(2, welcomeList.size());
		assertEquals("WELCOME-FILE1", (String) welcomeList.get(0));
		assertEquals("WELCOME-FILE2", (String) welcomeList.get(1));

	}
}
