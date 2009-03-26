package sdloader.javaee.webxml;

import sdloader.util.SystemPropertyUtil;

public class WebXmlWritingContext {

	protected StringBuilder builder = new StringBuilder();

	protected String SEP = SystemPropertyUtil.getLineSeparator();

	protected int depth = 0;

	protected String separator = "\t";

	public WebXmlWritingContext() {
	}

	public WebXmlWritingContext(String separator) {
		this.separator = separator;
	}

	public void appendHeader(String tag) {
		builder.append(tag);
		builder.append(SEP);
	}

	public void appendStartTag(String start) {
		builder.append(getSpaces() + start);
		builder.append(SEP);
		depth++;
	}

	public void appendEndTag(String end) {
		depth--;
		builder.append(getSpaces() + end);
		builder.append(SEP);
	}

	protected String getSpaces() {
		if (separator == null || separator.equals("")) {
			return "";
		}
		String s = "";
		for (int i = 0; i < depth; i++) {
			s += this.separator;
		}
		return s;
	}

	public void appendTag(String startTag, String value, String endTag) {
		builder.append(getSpaces() + startTag);
		builder.append(value);
		builder.append(endTag);
		builder.append(SEP);
	}

	public String getXml() {
		return new String(builder);
	}

	public void clear() {
		builder = new StringBuilder();
		depth = 0;
	}

}
