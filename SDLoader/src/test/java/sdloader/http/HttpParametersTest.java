/*
 * Copyright 2005-2010 the original author or authors.
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
package sdloader.http;

import junit.framework.TestCase;

public class HttpParametersTest extends TestCase {

	private String headerString = "GET /url?sa=Q&sa=R&hl=ja&q=test&btnG=Google+%E6%A4%9C%E7%B4%A2&lr=&ct=q HTTP/1.1\n"
			+ "Accept: */*\n"
			+ "Referer: http://www.google.co.jp/search?hl=ja&q=test&btnG=Google+%E6%A4%9C%E7%B4%A2&lr=\n"
			+ "Accept-Language: ja\n"
			+ "UA-CPU: x86\n"
			+ "Accept-Encoding: gzip, deflate\n"
			+ "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)\n"
			+ "Proxy-Connection: Keep-Alive\n" + "Host: www.google.co.jp\n";

	HttpRequestParameters params;

	@Override
	protected void setUp() throws Exception {
		HttpHeader header = new HttpHeader(headerString);
		HttpBody body = new HttpBody();
		params = new HttpRequestParameters(header, body);
		params.setBodyEncoding("UTF-8");
		params.setUseBodyEncodingForURI(true);
	}

	public void testParameter() {
		String hlValue = params.getParamter("hl");
		assertEquals("ja", hlValue);
		String btnValue = params.getParamter("btnG");
		assertEquals(btnValue, "Google 検索");
	}

	// 同一名のパラメータが複数あるときははじめのを返す
	public void testParameter2() {
		String saValue = params.getParamter("sa");
		assertEquals("Q", saValue);
	}

	public void testParameterValues() {
		String[] saValues = params.getParameterValues("sa");
		assertEquals(2, saValues.length);
		assertEquals("Q", saValues[0]);
		assertEquals("R", saValues[1]);
	}

}
