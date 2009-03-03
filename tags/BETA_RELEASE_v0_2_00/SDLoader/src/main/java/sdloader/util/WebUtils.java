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
package sdloader.util;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * パス関係のユーティリティ
 * 
 * @author c9katayama
 */
public class WebUtils {
	/**
	 * ヘッダー日付フォーマット
	 */
	private static final DateFormat HEADER_DATE_FORMAT = new SimpleDateFormat(
			"E, d MMM yyyy HH:mm:ss 'GMT'", Locale.UK);
	/**
	 * クッキー日付フォーマット
	 */
	private static final DateFormat COOKIE_DATE_FORMAT = new SimpleDateFormat(
			"E, d-MMM-yyyy HH:mm:ss 'GMT'", Locale.UK);

	private WebUtils() {
		super();
	}
	/**
	 * ContentType(text/html;charse=UTF-8など)からUTF-8部分を取り出します。
	 * @param value
	 * @return
	 */
	public static String parseCharsetFromContentType(String value){
		final String sepString = "charset=";
		int sep = value.indexOf(sepString);
		if(sep != -1){
			sep += sepString.length(); 
			int endIndex = value.length();
			int scIndex = value.indexOf(";",sep);
			if(scIndex!=-1)
				endIndex = scIndex;
			String charSet = value.substring(sep,endIndex);
			return charSet.trim();
		}else{
			return null;
		}
	}
	/**
	 * サポートされているエンコードかどうか
	 * サポートされていない場合、UnsupportedEncodingExceptionが発生します。
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static void checkSupportedEndcoding(String encoding) throws UnsupportedEncodingException{
		if(encoding==null)
			throw new UnsupportedCharsetException("Charset is null.");
		URLDecoder.decode("", encoding);
	}
	/**
	 * ヘッダー用の日付文字を、ミリ秒フォーマットに変換します。
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long parseHeaderDate(String date) throws ParseException {
		synchronized (HEADER_DATE_FORMAT) {
			return HEADER_DATE_FORMAT.parse(date).getTime();
		}
	}

	/**
	 * クッキー用の日付文字を、ミリ秒フォーマットに変換します。
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long parseCookieDate(String date) throws ParseException {
		synchronized (COOKIE_DATE_FORMAT) {
			return COOKIE_DATE_FORMAT.parse(date).getTime();
		}
	}

	/**
	 * 日付をヘッダー用の日付文字列にフォーマットします。
	 * 
	 * @param date
	 * @return
	 */
	public static String formatHeaderDate(Date date) {
		synchronized (HEADER_DATE_FORMAT) {
			return HEADER_DATE_FORMAT.format(date);
		}
	}

	/**
	 * 日付をクッキー用の日付文字列にフォーマットします。
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String formatCookieDate(Date date) {
		synchronized (COOKIE_DATE_FORMAT) {
			return COOKIE_DATE_FORMAT.format(date);
		}
	}
	
	public static URL[] createClassPaths(String targetDir, FileFilter fileFilter,boolean recursive){
		return createClassPaths(new File(targetDir), fileFilter, recursive);		
	}
	/**
	 * 対象ディレクトリ中のファイルへのURLを生成します。
	 * 
	 * @param targetDir
	 * @param fileFilter
	 * @param recursive 再帰的に追加するかどうか
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL[] createClassPaths(File targetDir, FileFilter fileFilter,boolean recursive){			

		if (!targetDir.exists())
			return null;

		List<URL> urlList = CollectionsUtil.newArrayList();

		File[] libs = targetDir.listFiles(fileFilter);
		if (libs != null) {
			for (int i = 0; i < libs.length; i++) {
				urlList.add(PathUtil.file2URL(libs[i]));
			}
		}
		if(recursive){
			File[] dirs = targetDir.listFiles();
			if (dirs != null) {
				for (int i = 0; i < dirs.length; i++) {
					if (dirs[i].isDirectory()) {
						URL[] urls = createClassPaths(dirs[i].getAbsolutePath(),
								fileFilter,recursive);
						if (urls != null) {
							for (int j = 0; j < urls.length; j++)
								urlList.add(urls[j]);
						}
					}
				}
			}
		}
		return (URL[]) urlList.toArray(new URL[] {});
	}
	/**
	 * リクエストURI中のコンテキストパス以外の部分を返します。 resoucePath = servletpath + pathinfo
	 * ただし、requestURIが"/"の場合(ルートアプリケーション）のみ、"/"を返します。
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */
	public static String getResourcePath(String contextPath,String requestURI) {		
		if (requestURI == null)
			return null;
		if (requestURI.equals("/")) {
			return "/";
		}
		if(contextPath.equals(requestURI)){
			return null;
		}else{
			String servletPath = requestURI.substring(contextPath.length(), requestURI.length());
			return servletPath;
		}
	}

	/**
	 * servletPath+PathInfoのパスから、サーブレットパスの部分を返します。 resoucePath = servletpath +
	 * pathinfo
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */
	public static String getServletPath(String pattern, String resourcePath) {
		if (resourcePath == null)
			return null;
		int type = matchPattern(pattern, resourcePath);
		switch (type) {
		case PATTERN_DEFAULT_MATCH:
			return "";
		case PATTERN_EXT_MATCH:
		case PATTERN_EXACT_MATCH:
			return resourcePath;
		case PATTERN_PATH_MATCH:
			return pattern.substring(0, pattern.length() - "/*".length());
		default:
			throw new RuntimeException("PATTERN_NOMATCH pattern=" + pattern
					+ " path=" + resourcePath);
		}
	}

	/**
	 * リクエストURI中のコンテキストパス以外の部分を返します.
	 * resoucePath = servletpath + pathinfo
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */
	public static String getPathInfo(String pattern, String resourcePath) {
		if (resourcePath == null)
			return null;
		int type = matchPattern(pattern, resourcePath);
		switch (type) {
		case PATTERN_DEFAULT_MATCH:
			return resourcePath;
		case PATTERN_EXT_MATCH:
		case PATTERN_EXACT_MATCH:
			return null;
		case PATTERN_PATH_MATCH:
			return resourcePath.substring(pattern.length() - "/*".length());
		default:
			throw new RuntimeException("PATTERN_NOMATCH pattern=" + pattern
					+ " path=" + resourcePath);
		}
	}

	/**
	 * パターンマッチなし
	 */
	public static final int PATTERN_NOMATCH = 0;
	/**
	 * デフォルトパターン(/*)でマッチ
	 */
	public static final int PATTERN_DEFAULT_MATCH = 1;
	/**
	 * 拡張子でマッチ
	 */
	public static final int PATTERN_EXT_MATCH = 2;
	/**
	 * パスでマッチ
	 */
	public static final int PATTERN_PATH_MATCH = 3;
	/**
	 * 完全マッチ
	 */
	public static final int PATTERN_EXACT_MATCH = 4;

	/**
	 * パスパターンと パスのマッチングを行います。
	 * 
	 * @param pattern
	 * @param path
	 * @return PATTERN_NOMATCH PATTERN_DEFAULT_MATCH PATTERN_EXT_MATCH
	 *         PATTERN_PATH_MATCH PATTERN_EXACT_MATCH
	 */
	public static int matchPattern(String pattern, String path) {
		if (pattern == null || path == null)
			return PATTERN_NOMATCH;

		if (pattern.equals(path))
			return PATTERN_EXACT_MATCH;

		if (pattern.equals("/*"))
			return PATTERN_DEFAULT_MATCH;

		if (pattern.endsWith("/*")) {
			if (pattern.regionMatches(0, path, 0, pattern.length() - 2)) {
				if (path.length() == (pattern.length() - 2))
					return PATTERN_PATH_MATCH;
				else if (path.charAt(pattern.length() - 2) == '/')
					return PATTERN_PATH_MATCH;
			}
			return PATTERN_NOMATCH;
		}

		if (pattern.startsWith("*.")) {
			int resourceDot = path.lastIndexOf(".");
			if (resourceDot >= 0) {
				String resourceExt = path.substring(resourceDot, path.length());
				int patternDot = pattern.indexOf(".");
				String patternExt = pattern.substring(patternDot, pattern
						.length());
				if (resourceExt.equals(patternExt))
					return PATTERN_EXT_MATCH;
			}
		}
		return PATTERN_NOMATCH;
	}

	/**
	 * Query部分を取り除きます
	 * 
	 * @param requestURI
	 * @return
	 */
	public static String stripQueryPart(String requestURI) {
		int queryIndex = requestURI.indexOf("?");
		if (queryIndex > 0) {
			return requestURI.substring(0, queryIndex);
		} else {
			return requestURI;
		}
	}

	/**
	 * Queary部分を取得します。ない場合、nullを返します。
	 * 
	 * @param requestURI
	 * @return
	 */
	public static String getQueryPart(String requestURI) {
		int queryIndex = requestURI.indexOf("?");
		if (queryIndex > 0) {
			return requestURI.substring(queryIndex + 1, requestURI.length());
		} else {
			return null;
		}
	}

	/**
	 * RequestURLを構築します。
	 * 
	 * @param scheme
	 * @param host
	 * @param port
	 * @param requestURI
	 * @return
	 */
	public static StringBuffer buildRequestURL(String scheme, String host,
			int port, String requestURI) {
		String portString;
		if (port == 80 && scheme.equals("http"))
			portString = "";
		else if (port == 443 && scheme.equals("https"))
			portString = "";
		else
			portString = ":" + port;
		requestURI = stripQueryPart(requestURI);
		return new StringBuffer(scheme + "://" + host + portString
				+ requestURI);
	}
	/**
	 * RequestURLを構築します。
	 * host部分にポート指定が無い場合、schemeからポートを
	 * 設定します。
	 * @param scheme
	 * @param host
	 * @param port
	 * @param requestURI
	 * @return
	 */
	public static StringBuffer buildRequestURL(String scheme, String host,String requestURI) {
		int portSep = host.indexOf(":");
		String portString;
		if(portSep != -1){
			portString = host.substring(portSep+1);
			host = host.substring(0,portSep);
		}else{
			if (scheme.equals("http"))
				portString = "";
			else if (scheme.equals("https"))
				portString = "";
			else
				throw new RuntimeException("schema:"+scheme+" not support.");
		}
		return buildRequestURL(scheme, host,Integer.parseInt(portString),requestURI);
	}
}
