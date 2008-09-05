package examples.page;

import org.t2framework.annotation.composite.POST;
import org.t2framework.annotation.core.ActionParam;
import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.navigation.Redirect;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

@RequestScope
@Page("checkbox")
public class CheckboxPage {

	@Default
	public Navigation index(WebContext context) {
		context.getRequest().setAttribute("check", Boolean.TRUE);
		return Redirect.to("/jsp/checkbox.jsp");
	}

	@POST
	@ActionParam
	public Navigation submit(WebContext context) {
		String checkValue = context.getRequest().getParameter("check");
		if ("true".equals(checkValue) || "on".equals(checkValue)) {
			context.getRequest().setAttribute("value", Boolean.TRUE);
			context.getRequest().setAttribute("check", Boolean.TRUE);
		} else {
			context.getRequest().setAttribute("value", Boolean.FALSE);
			context.getRequest().setAttribute("check", Boolean.FALSE);
		}
		return Forward.to("/jsp/checkbox.jsp");
	}

}
