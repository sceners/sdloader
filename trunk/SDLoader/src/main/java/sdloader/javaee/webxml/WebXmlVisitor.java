package sdloader.javaee.webxml;

public interface WebXmlVisitor {

	void visit(WebAppTag tag);

	void visit(ContextParamTag contextParamTag);

	void visit(FilterTag filterTag);

	void visit(InitParamTag initParamTag);

	void visit(FilterMappingTag filterMappingTag);

	void visit(ListenerTag listenerTag);

	void visit(ServletTag servletTag);

	void visit(ServletMappingTag servletMappingTag);

	void visit(WelcomeFileListTag welcomeFileListTag);

	void visit(ErrorPageTag errorPageTag);

	WebXmlWritingContext getContext();
}
