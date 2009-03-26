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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import sdloader.SDLoader;
import sdloader.internal.SDLoaderConfig;
import sdloader.util.CollectionsUtil;

/**
 * HTTPパラメータ
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpRequestParameters {

	private Map<String, String[]> paramMap;

	private List<String> paramNameList;

	// パラメータをデコードする際のデフォルトエンコーディング
	private String defaultEncoding;

	// body部分のエンコーディング
	private String bodyEncoding;

	// GETのQueryに対して、bodyEncodingを使用するかどうか
	private boolean useBodyEncodingForURI;

	private HttpHeader header;
	private HttpBody body;

	private boolean parameterInitialized;

	public HttpRequestParameters(HttpHeader header, HttpBody body) {
		this.header = header;
		this.body = body;

		// TODO RequestScopeからのパラメータ取得はどこかにまとめるべき
		String encode = "ISO-8859-1";
		boolean useBodyEncode = true;
		SDLoader loader = ProcessScopeContext.getContext().getAttribute(
				SDLoader.class);
		if (loader != null) {
			SDLoaderConfig config = loader.getSDLoaderConfig();
			encode = config.getConfigString(
					HttpRequest.KEY_REQUEST_DEFAULT_ENCODE, encode);
			useBodyEncode = config.getConfigBoolean(
					HttpRequest.KEY_REQUEST_USE_BODY_ENCODEING_FOR_URI,
					useBodyEncode);

		}
		setDefaultEncoding(encode);
		setUseBodyEncodingForURI(useBodyEncode);
	}

	protected void initIfNeed() {
		if (!parameterInitialized) {
			initParameters();
			parameterInitialized = true;
		}
	}

	/**
	 * HttpRequestParameters#getParameterが呼ばれた初回に呼ばれます。
	 */
	private void initParameters() {
		paramMap = CollectionsUtil.newHashMap();
		paramNameList = CollectionsUtil.newLinkedList();
		if (header.getQueryString() != null) {
			String queryEncoding = (isUseBodyEncodingForURI()) ? bodyEncoding
					: defaultEncoding;
			parseRequestQuery(header.getQueryString(), queryEncoding);
		}
		byte[] bodyData = body.getBodyData();
		if (bodyData != null
				&& header.getMethod().equalsIgnoreCase(HttpConst.POST)) {
			String contType = header.getHeaderValue(HttpConst.CONTENTTYPE);
			if (contType != null) {
				contType = contType.toLowerCase();
				if (contType.indexOf(HttpConst.WWW_FORM_URLENCODE) != -1) {
					try {
						String bodyPartQueryString = new String(bodyData);
						parseRequestQuery(bodyPartQueryString, bodyEncoding);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
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
			String value = "";
			if (token.hasMoreTokens()) {
				value = token.nextToken();
			}
			try {
				key = decode(key, encode);
				value = decode(value, encode);
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
			addParameter(key, value);
		}
	}

	private String decode(String value, String encode)
			throws UnsupportedEncodingException {
		// ISO-8859-1を使用すると、ダイレクトに入力バイトが取れる。
		// バイトを取ってエンコード
		value = URLDecoder.decode(value, "ISO-8859-1");
		value = new String(value.getBytes("ISO-8859-1"), encode);
		return value;
	}

	public String getParamter(String key) {
		initIfNeed();
		String[] paramList = paramMap.get(key);
		if (paramList == null) {
			return null;
		}
		String param = paramList[0];
		return param;
	}

	public String[] getParamterValues(String key) {
		initIfNeed();
		String[] params = paramMap.get(key);
		if (params == null) {
			return null;
		}
		return params;
	}

	public Iterator<String> getParameterNames() {
		initIfNeed();
		return paramNameList.iterator();
	}

	public Map<String, String[]> getParamterMap() {
		initIfNeed();
		Map<String, String[]> newMap = CollectionsUtil.newHashMap();
		newMap.putAll(paramMap);
		return newMap;
	}

	/**
	 * デフォルトのエンコーディングをセットします。 bodyEncodingもこのエンコーディングに変更されます。
	 * 
	 * @param defaultEncoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		setBodyEncoding(defaultEncoding);
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * <pre>
	 * body部分のエンコーディングをセットします。 
	 * useBodyEncodingForURIがtrueの場合は、GETパラメータにもこのエンコードが適用されます。
	 * </pre>
	 * 
	 * @param bodyEncoding
	 */
	public void setBodyEncoding(String bodyEncoding) {
		this.bodyEncoding = bodyEncoding;
		parameterInitialized = false;
	}

	public String getBodyEncoding() {
		return bodyEncoding;
	}

	public void setUseBodyEncodingForURI(boolean useBodyEncodingForURI) {
		this.useBodyEncodingForURI = useBodyEncodingForURI;
		parameterInitialized = false;
	}

	public boolean isUseBodyEncodingForURI() {
		return useBodyEncodingForURI;
	}

	private void addParameter(String key, String value) {
		if (value == null) {
			return;
		}
		String[] params = paramMap.get(key);
		if (params == null) {
			params = new String[] { value };
			paramMap.put(key, params);
			paramNameList.add(key);
		} else {
			String[] newParams = new String[params.length + 1];
			System.arraycopy(params, 0, newParams, 0, params.length);
			newParams[newParams.length - 1] = value;
			paramMap.put(key, newParams);
		}
	}
}
