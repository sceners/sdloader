package test.loopstart.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

public class LoopServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String n;

	@Override
	public void init() throws ServletException {
		n = "hogehoge";
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Assert.assertEquals(n, "hogehoge");
		resp.getWriter().write(
				req.getParameter("loop") + req.getAttribute("filter"));

	}

	@Override
	public void destroy() {
		Assert.assertEquals(n, "hogehoge");
		n = null;
	}
}
