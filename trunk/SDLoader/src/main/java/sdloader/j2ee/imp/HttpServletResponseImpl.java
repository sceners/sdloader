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
package sdloader.j2ee.imp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import sdloader.http.HttpConst;
import sdloader.http.HttpResponseHeader;
import sdloader.util.WebUtils;

/**
 * HttpServletResponse実装クラス
 * 
 * @author c9katayama
 */
public class HttpServletResponseImpl implements HttpServletResponse {
	private Locale locale = Locale.getDefault();
	
	private String characterEncoding = "UTF-8";//HttpParameters.DEFAULT_CHAR_ENCODE;

	private HttpResponseHeader header = new HttpResponseHeader();

	private ServletOutputStreamImpl servletOutputStream = new ServletOutputStreamImpl();

	private PrintWriter writer;

	private int bufferSize;

	public HttpServletResponseImpl() {
		super();
	}

	public void addCookie(Cookie cookie) {
		header.addCookie(cookie);
	}

	public boolean containsHeader(String name) {
		return header.containsHeader(name);
	}

	public String getContentType() {
		return header.getHeader(HttpConst.CONTENTTYPE);
	}

	public void setContentLength(int length) {
		header.addHeader(HttpConst.CONTENTLENGTH, String.valueOf(length));
	}

	public void setContentType(String type) {
		header.addHeader(HttpConst.CONTENTTYPE, type);
	}

	public void setStatus(int sc) {
		header.setStatusCode(sc);
		header.setStatus(HttpConst.findStatus(sc));
	}

	public void setStatus(int sc, String msg) {
		header.setStatus(msg);
		header.setStatusCode(sc);
	}

	public String encodeURL(String url) {
		return url;
	}

	public String encodeRedirectURL(String url) {
		return url;
	}

	public String encodeUrl(String uri) {
		return uri;
	}

	public String encodeRedirectUrl(String url) {
		return url;
	}

	public void sendError(int error, String message) throws IOException {
		setStatus(error,message);
		this.servletOutputStream.flush();
		this.servletOutputStream.close();
	}

	public void sendError(int error) throws IOException {
		setStatus(error);
		this.servletOutputStream.flush();
		this.servletOutputStream.close();
	}

	public void sendRedirect(String path) throws IOException {
		header.addHeader(HttpConst.LOCATION,path);
		setStatus(HttpConst.SC_TEMPORARY_REDIRECT);
		this.servletOutputStream.flush();
		this.servletOutputStream.close();
	}

	public void setDateHeader(String name, long time) {
		setHeader(name, WebUtils.formatHeaderDate(new Date(time)));
	}

	public void addDateHeader(String name, long time) {
		addHeader(name, WebUtils.formatHeaderDate(new Date(time)));
	}

	public void setHeader(String name, String value) {
		this.header.addHeader(name, value);
	}

	public void addHeader(String name, String value) {
		this.header.addHeader(name, value);
	}

	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));
	}

	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return servletOutputStream;
	}

	public PrintWriter getWriter() throws IOException {		
		if (writer == null)
			writer = new PrintWriter(servletOutputStream);
		return writer;
	}

	public void setCharacterEncoding(String encoding){
		this.characterEncoding = encoding;
	}

	public void setBufferSize(int size) {
		this.bufferSize = size;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void flushBuffer() throws IOException {
		if (writer != null)
			writer.flush();
		servletOutputStream.flush();
	}

	public void resetBuffer() {
		servletOutputStream = new ServletOutputStreamImpl();
	}

	public boolean isCommitted() {
		return servletOutputStream.isClosed();
	}

	public void reset() {
		servletOutputStream = new ServletOutputStreamImpl();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	// /non interface method
	public byte[] getBodyData() throws IOException {
		flushBuffer();
		servletOutputStream.close();
		return servletOutputStream.getOutputData();
	}

	public HttpResponseHeader getResponseHeader() {
		return header;
	}
}
