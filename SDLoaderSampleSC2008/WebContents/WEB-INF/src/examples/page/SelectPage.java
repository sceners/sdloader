package examples.page;

import java.util.Map;

import org.t2framework.annotation.composite.POST;
import org.t2framework.annotation.core.ActionParam;
import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.Request;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.navigation.Redirect;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;
import commons.util.CollectionsUtil;

@RequestScope
@Page("select")
public class SelectPage {

	private static Map<Integer, String> map = CollectionsUtil.newHashMap();

	static {
		map.put(new Integer(0), "AAA");
		map.put(new Integer(1), "BBB");
		map.put(new Integer(2), "CCC");
	}

	@Default
	public Navigation index(WebContext context) {
		return Redirect.to("jsp/select.jsp");
	}

	@POST
	@ActionParam
	public Navigation submit(WebContext context) {
		Request request = context.getRequest();
		Integer moge = Integer.valueOf(request.getParameter("moge"));
		request.setAttribute("moge", moge);
		request.setAttribute("target", map.get(moge));
		return Forward.to("/jsp/select.jsp");
	}

}
