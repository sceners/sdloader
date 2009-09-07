package test.loadonstartup.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import junit.framework.Assert;

@SuppressWarnings("serial")
public class Servlet2 extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("Servlet2 init.");
		Assert.assertEquals(1, StartUpNum.startupNum++);
	}

}
