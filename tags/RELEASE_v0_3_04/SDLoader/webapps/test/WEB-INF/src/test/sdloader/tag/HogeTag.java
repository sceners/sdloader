package test.sdloader.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class HogeTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {

		try {
			pageContext.getOut().write("HOGEHOGE");
		} catch (IOException ioe) {
			throw new JspException(ioe);
		}

		return super.doStartTag();
	}
}
