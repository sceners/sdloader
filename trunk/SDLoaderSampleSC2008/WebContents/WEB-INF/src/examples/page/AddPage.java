package examples.page;

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
import commons.util.StringUtil;

import examples.MessageConstants;

/**
 * ベーシックな足し算の例.バリデーションも自前.
 * 
 * @param context
 * @return
 */
@RequestScope
@Page("add")
public class AddPage {

	@Default
	public Navigation index(WebContext context) {
		return Redirect.to("/jsp/add.jsp");
	}

	@POST
	@ActionParam
	public Navigation add(WebContext context) {
		final Request request = context.getRequest();
		final String s1 = request.getParameter("arg1");
		final String s2 = request.getParameter("arg2");
		request.setAttribute("arg1", s1);
		request.setAttribute("arg2", s2);
		if (isBothEmpty(s1, s2)) {
			request.setAttribute("message",
					MessageConstants.ADD_ERROR_REQUIRED_MESSAGE);
			return Forward.to("/jsp/add.jsp");
		}
		Integer result = null;
		try {
			Integer arg1 = Integer.valueOf(s1);
			Integer arg2 = Integer.valueOf(s2);
			result = new Integer(arg1.intValue() + arg2.intValue());
			request.setAttribute("result", result);
		} catch (Exception e) {
			request.setAttribute("message", MessageConstants.ADD_ERROR_MESSAGE);
		}
		return Forward.to("/jsp/add.jsp");
	}

	protected boolean isBothEmpty(String s1, String s2) {
		return StringUtil.isEmpty(s1) || StringUtil.isEmpty(s2);
	}

	@POST
	@ActionParam
	public Navigation addAndMove(WebContext context) {
		final Request request = context.getRequest();
		final String s1 = request.getParameter("arg1");
		final String s2 = request.getParameter("arg2");
		request.setAttribute("arg1", s1);
		request.setAttribute("arg2", s2);
		if (isBothEmpty(s1, s2)) {
			request.setAttribute("message",
					MessageConstants.ADD_ERROR_REQUIRED_MESSAGE);
			return Forward.to("/jsp/add.jsp");
		}
		Integer result = null;
		try {
			Integer arg1 = Integer.valueOf(s1);
			Integer arg2 = Integer.valueOf(s2);
			result = new Integer(arg1.intValue() + arg2.intValue());
			request.setAttribute("result", result);
		} catch (Exception e) {
			request.setAttribute("message", MessageConstants.ADD_ERROR_MESSAGE);
		}
		return Forward.to("/jsp/addResult.jsp");
	}

	// 下記は0.4から.
	/*
	 * @POST
	 * 
	 * @ActionParam public Navigation addWithForm(@Form AddForm dto,
	 * TeedaContext context) { Request request = context.getRequest();
	 * request.setAttribute("result", new Integer(dto.getArg1().intValue() +
	 * dto.getArg2().intValue())); return Forward.to("jsp/add.jsp"); }
	 */

}
