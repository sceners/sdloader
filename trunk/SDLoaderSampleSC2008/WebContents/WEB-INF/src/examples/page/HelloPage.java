package examples.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucy.annotation.core.Inject;

import org.t2framework.annotation.core.ActionPath;
import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

import examples.service.HelloService;

/**
 * シンプルなHello Worldサンプル.
 * 
 * @author shot
 */
/* http://yourdomain/t2-samples/helloでマッピング. */
@RequestScope
@Page("hello")
public class HelloPage {

	protected HelloService helloService;

	@Default
	public Navigation index(WebContext context) {
		context.getRequest().setAttribute("greet", helloService.hello());
		return Forward.to("/jsp/hello.jsp");
	}

	/**
	 * http://yourdomain/t2-samples/hello/requestというURLで呼ばれる.
	 */
	@ActionPath
	public Navigation request(HttpServletRequest request) {
		System.out.println("request.getContextPath() : "
				+ request.getContextPath());
		request.setAttribute("greet", helloService.hello()
				+ " from request().");
		return Forward.to("/jsp/hello.jsp");
	}

	/**
	 * &#064;ActionPathでvalueとして、"struts"としているので、
	 * http://yourdomain/t2-samples/hello/strutsというURLの場合呼ばれる.
	 */
	@ActionPath("struts")
	public Navigation likeStrutsType(HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("request.getContextPath() : "
				+ request.getContextPath());
		request.setAttribute("greet", helloService.hello()
				+ " from likeStrutsType().");
		return Forward.to("/jsp/hello.jsp");
	}

	/**
	 * Lucyによってインジェクトされる.
	 * 
	 * @param helloService
	 */
	@Inject
	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

}
