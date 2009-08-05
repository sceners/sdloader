package test.loadonstartup.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import junit.framework.Assert;

@SuppressWarnings("serial")
public class Servlet5 extends HttpServlet {

	@Override
	public void init() throws ServletException {
		Assert.assertEquals(2, StartUpNum.startupNum++);
	}
}