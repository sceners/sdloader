package test.loopstart.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.Assert;

public class LoopFilter implements Filter {

	private String n;

	public void destroy() {
		Assert.assertEquals(n, "hogemoge");
		n = null;
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		Assert.assertEquals(n, "hogemoge");
		req.setAttribute("filter", req.getParameter("filter"));
		chain.doFilter(req, res);
	}

	public void init(FilterConfig arg0) throws ServletException {
		n = "hogemoge";
	}
}
