/*
 * Copyright 2005-2009 the original author or authors.
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
package sdloader.util;

import sdloader.util.WebUtil;
import junit.framework.TestCase;

public class WebUtilsTest extends TestCase {

	// from servlet-2_4_fr_sec.pdf
	String pattern1 = "/foo/bar/*";
	String pattern2 = "/baz/*";
	String pattern3 = "/catalog";
	String pattern4 = "*.bop";
	String pattern5 = "/*";

	public void testPathMatch() {
		assertEquals(WebUtil.PATTERN_PATH_MATCH, WebUtil.matchPattern(pattern1,
				"/foo/bar/index.html"));
		assertEquals(WebUtil.PATTERN_PATH_MATCH, WebUtil.matchPattern(pattern1,
				"/foo/bar/index.bop"));
		assertEquals(WebUtil.PATTERN_PATH_MATCH, WebUtil.matchPattern(pattern2,
				"/baz"));
		assertEquals(WebUtil.PATTERN_PATH_MATCH, WebUtil.matchPattern(pattern2,
				"/baz/index.html"));
		assertEquals(WebUtil.PATTERN_EXACT_MATCH, WebUtil.matchPattern(
				pattern3, "/catalog"));
		assertEquals(WebUtil.PATTERN_DEFAULT_MATCH, WebUtil.matchPattern(
				pattern5, "/catalog/index.html"));
		assertEquals(WebUtil.PATTERN_EXT_MATCH, WebUtil.matchPattern(pattern4,
				"/catalog/racecar.bop"));
		assertEquals(WebUtil.PATTERN_EXT_MATCH, WebUtil.matchPattern(pattern4,
				"/index.bop"));

	}

	public void testServletPath() {

		assertEquals("/foo/bar", WebUtil.getServletPath(pattern1,
				"/foo/bar/test.jsp"));
		assertEquals("/baz", WebUtil.getServletPath(pattern2,
				"/baz/test/tt.jsp"));
		assertEquals("/catalog", WebUtil.getServletPath(pattern3, "/catalog"));
		assertEquals("/test/a.bop", WebUtil.getServletPath(pattern4,
				"/test/a.bop"));
		assertEquals("", WebUtil.getServletPath(pattern5, "/test/a.bop"));

	}

	public void testPathInfo() {
		assertEquals("/test.jsp", WebUtil.getPathInfo(pattern1,
				"/foo/bar/test.jsp"));
		assertEquals("/test/tt.jsp", WebUtil.getPathInfo(pattern2,
				"/baz/test/tt.jsp"));
		assertEquals(null, WebUtil.getPathInfo(pattern3, "/catalog"));
		assertEquals(null, WebUtil.getPathInfo(pattern4, "/test/a.bop"));
		assertEquals("/test/a.bop", WebUtil
				.getPathInfo(pattern5, "/test/a.bop"));

	}

	public void testParseCharsetFromContentType() {
		String v = "text/html;charset=Windows-31J";
		assertEquals("Windows-31J", WebUtil.parseCharsetFromContentType(v));

		v = "text/html;charset= Windows-31J ";
		assertEquals("Windows-31J", WebUtil.parseCharsetFromContentType(v));

		v = "text/html;charset= Windows-31J ; ";
		assertEquals("Windows-31J", WebUtil.parseCharsetFromContentType(v));
	}
}
