package examples.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.t2framework.annotation.composite.GET;
import org.t2framework.annotation.composite.POST;
import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.navigation.Redirect;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

/**
 * This example shows you T2 can do like Servlet3.0:P
 * 
 * @author shot
 * 
 */
@RequestScope
@Page("getandpost")
public class GetAndPostPage {

	@Default
	public Navigation index(WebContext context) {
		System.out.println("called");
		return Redirect.to("jsp/simpleGetAndPost.jsp");
	}

	@POST
	public Navigation post(HttpServletRequest request,
			HttpServletResponse response) {
		request.setAttribute("message", "Do POST.");
		return Forward.to("jsp/simpleGetAndPost.jsp");
	}

	@GET
	public Navigation get(HttpServletRequest request,
			HttpServletResponse response) {
		request.setAttribute("message", "Do GET.");
		return Forward.to("jsp/simpleGetAndPost.jsp");
	}

}
