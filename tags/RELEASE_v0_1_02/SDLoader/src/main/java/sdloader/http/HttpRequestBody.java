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
package sdloader.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

/**
 * HTTPリクエストのボディー部分
 * 
 * @author c9katayama
 */
public class HttpRequestBody {
	private HttpParameters parameters;

	private HttpRequestHeader header;

	private byte[] bodyData;

	public HttpRequestBody(HttpRequestHeader header) {
		this.header = header;
		parameters = new HttpParameters(this);

	}

	public HttpRequestBody(HttpRequestHeader header, byte[] bodyData) {
		this.header = header;
		this.bodyData = bodyData;
		parameters = new HttpParameters(this);
	}

	/**
	 * HttpParameters#getParameterが呼ばれた初回に呼ばれます。
	 */
	void initParameters() {
		if (header.getQueryString() != null)
			parseRequestQuery(header.getQueryString(), "UTF-8");// GETはUTF-8
		if (bodyData != null && header.getMethod().equals(HttpConst.POST)) {
			String contType = header.getHeader(HttpConst.CONTENTTYPE);
			if (contType != null
					&& contType.equals(HttpConst.WWW_FORM_URLENCODE)) {
				try {
					parseRequestQuery(new String(bodyData,
							HttpParameters.DEFAULT_CHAR_ENCODE), parameters
							.getCharacterEncoding());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void parseRequestQuery(String query, String encode) {
		StringTokenizer paramToken = new StringTokenizer(query, "&", false);

		while (paramToken.hasMoreElements()) {
			String param = paramToken.nextToken();
			StringTokenizer token = new StringTokenizer(param, "=", false);
			String key = token.nextToken();
			String value = null;
			if (token.hasMoreTokens()) {
				value = token.nextToken();
				try {
					key = decode(key, encode);
					value = decode(value, encode);
				} catch (UnsupportedEncodingException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			}
			parameters.addParameter(key, value);
		}
	}

	private String decode(String value, String encode)
			throws UnsupportedEncodingException {
		value = URLDecoder.decode(value, HttpParameters.DEFAULT_CHAR_ENCODE);
		value = new String(value.getBytes(HttpParameters.DEFAULT_CHAR_ENCODE),
				encode);
		return value;
	}

	public byte[] getBodyData() {
		return bodyData;
	}

	public HttpRequestHeader getHeader() {
		return header;
	}

	public HttpParameters getParameters() {
		return parameters;
	}
}
