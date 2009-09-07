package test.requesturl.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

public class RequestURLServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURL = req.getRequestURL().toString();
		String requestURI = req.getRequestURI();

		Assert.assertEquals("http://localhost:8190/requesturl/hoge.do",
				requestURL);
		Assert.assertEquals("/requesturl/hoge.do", requestURI);

	}
}
