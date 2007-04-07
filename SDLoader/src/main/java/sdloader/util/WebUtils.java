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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	 * ヘッダー用の日付文字を、ミリ秒フォーマットに変換します。
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long parseHeaderDate(String date) throws ParseException{
		synchronized(HEADER_DATE_FORMAT){
			return HEADER_DATE_FORMAT.parse(date).getTime();
		}
	}
	/**
	 * クッキー用の日付文字を、ミリ秒フォーマットに変換します。
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long parseCookieDate(String date) throws ParseException{
		synchronized(COOKIE_DATE_FORMAT){
			return COOKIE_DATE_FORMAT.parse(date).getTime();
		}
	}
	/**
	 * 日付をヘッダー用の日付文字列にフォーマットします。
	 * @param date
	 * @return
	 */
	public static String formatHeaderDate(Date date){
		synchronized(HEADER_DATE_FORMAT){
			return HEADER_DATE_FORMAT.format(date);
		}
	}
	/**
	 * 日付をクッキー用の日付文字列にフォーマットします。
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String formatCookieDate(Date date){
		synchronized(COOKIE_DATE_FORMAT){
			return COOKIE_DATE_FORMAT.format(date);
		}
	}
	/**
	 * 対象ディレクトリ中のファイルへのURLを生成します。
	 * ディレクトリがある場合、再帰的にファイルを探します。
	 * @param targetDir
	 * @param fileFilter
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL[] createClassPaths(String targetDir,FileFilter fileFilter)
			throws MalformedURLException {
		File libDir = new File(targetDir);
		if (!libDir.exists())
			return null;

		List urlList = new ArrayList();

		File[] libs = libDir.listFiles(fileFilter);
		if (libs != null) {
			for (int i = 0; i < libs.length; i++) {
				String libPath = libs[i].getAbsolutePath();
				libPath = WebUtils.replaceFileSeparator(libPath);
				urlList.add(new URL("file:///" + libPath));
			}
		}

		File[] dirs = libDir.listFiles();
		if (dirs != null) {
			for (int i = 0; i < dirs.length; i++) {
				if (dirs[i].isDirectory()) {
					URL[] urls = createClassPaths(dirs[i]
							.getAbsolutePath(),fileFilter);
					if (urls != null) {
						for (int j = 0; j < urls.length; j++)
							urlList.add(urls[j]);
					}
				}
			}
		}

		return (URL[]) urlList.toArray(new URL[] {});
	}

	/**
	 * リクエストURI中のコンテキストパス部分を返します。
	 * requestURI = contextPath + servletPath + pathInfo
	 * @param requestURI
	 * @return contextPath
	 */
	public static String getContextPath(String requestURI) {
		if (requestURI == null)
			return null;

		if (requestURI.startsWith("/")) {
			if (requestURI.indexOf("/", 1) != -1) {
				String contextPath = requestURI.substring(0, requestURI
						.indexOf("/", 1));
				return contextPath;
			}
			return requestURI;
		}
		return null;
	}

	/**
	 * リクエストURI中のコンテキストパス以外の部分を返します。 
	 * resoucePath = servletpath + pathinfo
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */
	public static String getResourcePath(String requestURI) {
		if (requestURI == null)
			return null;

		if (requestURI.startsWith("/") && requestURI.length() > 1
				&& requestURI.indexOf("/", 1) >= 1) {
			String servletPath = requestURI.substring(requestURI
					.indexOf("/", 1), requestURI.length());
			return servletPath;
		}
		return null;
	}
	/**
	 * servletPath+PathInfoのパスから、サーブレットパスの部分を返します。 
	 * resoucePath = servletpath + pathinfo
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */
	public static String getServletPath(String pattern,String resourcePath){
		int type = matchPattern(pattern,resourcePath);
		switch(type){
			case PATTERN_DEFAULT_MATCH:
				return "";
			case PATTERN_EXT_MATCH:
			case PATTERN_EXACT_MATCH:
				 return resourcePath;
			case PATTERN_PATH_MATCH:
				return pattern.substring(0,pattern.length()-"/*".length());
			default:
				throw new RuntimeException("PATTERN_NOMATCH pattern="+pattern+" path="+resourcePath);
		}
	}
	/**
	 * リクエストURI中のコンテキストパス以外の部分を返します。 
	 * resoucePath = servletpath + pathinfo
	 * 
	 * @param requestURI
	 * @return resourcePath
	 */	
	public static String getPathInfo(String pattern,String resourcePath){
		int type = matchPattern(pattern,resourcePath);
		switch(type){
			case PATTERN_DEFAULT_MATCH:
				return resourcePath;
			case PATTERN_EXT_MATCH:
			case PATTERN_EXACT_MATCH:
				return null;
			case PATTERN_PATH_MATCH:
				return resourcePath.substring(pattern.length()-"/*".length());
			default:
				throw new RuntimeException("PATTERN_NOMATCH pattern="+pattern+" path="+resourcePath);
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
	 * パスとパスパターンのマッチングを行います。
	 * 
	 * @param pattern
	 * @param resourcePath
	 * @return
	 */
	public static int matchPattern(String pattern, String resourcePath) {
		if (pattern == null || resourcePath == null)
			return PATTERN_NOMATCH;

		if (pattern.equals(resourcePath))
			return PATTERN_EXACT_MATCH;

		if (pattern.equals("/*"))
			return PATTERN_DEFAULT_MATCH;

		if (pattern.endsWith("/*")) {
			if (pattern.regionMatches(0, resourcePath, 0, pattern.length() - 2)) {
				if (resourcePath.length() == (pattern.length() - 2))
					return PATTERN_PATH_MATCH;
				else if (resourcePath.charAt(pattern.length() - 2) == '/')
					return PATTERN_PATH_MATCH;
			}
			return PATTERN_NOMATCH;
		}

		if (pattern.startsWith("*.")) {
			int resourceDot = resourcePath.lastIndexOf(".");
			if (resourceDot >= 0) {
				String resourceExt = resourcePath.substring(resourceDot,
						resourcePath.length());
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
	 * ストリームをコピーします。
	 * 
	 * @param in
	 * @param out
	 * @return コピーしたバイト数
	 * @throws IOException
	 */
	public static final int copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[8196];
		int totalSize = 0;
		int size = 0;
		while ((size = in.read(buf)) != -1) {
			out.write(buf, 0, size);
			totalSize += size;
		}
		return totalSize;
	}

	/**
	 * パス中の\\を/に置き換えます。
	 * 
	 * @param filepath
	 * @return
	 */
	public static final String replaceFileSeparator(String filepath) {
		return filepath.replace('\\', '/');
	}
	/**
	 * Query部分を取り除きます
	 * @param requestURI
	 * @return
	 */
	public static String stripQueryPart(String requestURI){
		int queryIndex = requestURI.indexOf("?");
		if(queryIndex>0){
			return requestURI.substring(0,queryIndex);
		}else{
			return requestURI;
		}
	}
	/**
	 * Queary部分を取得します。ない場合、nullを返します。
	 * @param requestURI
	 * @return
	 */
	public static String getQueryPart(String requestURI){
		int queryIndex = requestURI.indexOf("?");
		if(queryIndex>0){
			return requestURI.substring(queryIndex+1,requestURI.length());
		}else{
			return null;
		}
	}
	/**
	 * RequestURLを構築します。
	 * @param schema
	 * @param localName
	 * @param port
	 * @param requestURI
	 * @return
	 */
	public static StringBuffer buildRequestURL(String schema,String localName,int port,String requestURI){
		String portString;
		if(port==80 && schema.equals("http"))
			portString = "";
		else if(port==443 && schema.equals("https"))
			portString = "";
		else
			portString = ":"+port;
		requestURI = stripQueryPart(requestURI);
		return new StringBuffer(schema+"://"+localName+portString+requestURI);
	}
}
