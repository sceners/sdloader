/**
 * 
 */
package sdloader.javaee.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
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
			public void close() throws IOException {
			}
		};
	}
}