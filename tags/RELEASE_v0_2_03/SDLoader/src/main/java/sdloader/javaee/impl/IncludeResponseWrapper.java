/**
 * 
 */
package sdloader.javaee.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * RequestDispatcher#include()時のResponseWrapper
 * 
 * @author AKatayama
 * 
 */
public class IncludeResponseWrapper extends HttpServletResponseWrapper {
	public IncludeResponseWrapper(HttpServletResponse res) {
		super(res);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(super.getWriter()) {
			@Override
			public void close() {
			}
		};
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		final ServletOutputStream out = super.getOutputStream();
		return new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				out.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b, off, len);
			}

			@Override
			public void close() throws IOException {
			}
		};
	}

	@Override
	public void sendRedirect(String location) throws IOException {
	}

	@Override
	public void setBufferSize(int size) {
	}

	@Override
	public void setCharacterEncoding(String charset) {
	}

	@Override
	public void setContentLength(int len) {
	}

	@Override
	public void setDateHeader(String name, long date) {
	}

	@Override
	public void setIntHeader(String name, int value) {
	}

	@Override
	public void setLocale(Locale loc) {
	}

	@Override
	public void setStatus(int sc) {
	}

	@Override
	public void setStatus(int sc, String sm) {
	}

	@Override
	public void sendError(int sc) throws IOException {
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
	}

	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void setContentType(String type) {
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public void addDateHeader(String name, long date) {
	}

	@Override
	public void addIntHeader(String name, int value) {
	}

	@Override
	public void addCookie(Cookie cookie) {
	}
}