package sdloader.javaee.webxml.impl;

import java.io.IOException;
import java.io.Writer;

import sdloader.exception.IORuntimeException;
import sdloader.javaee.webxml.WebXml;
import sdloader.javaee.webxml.WebXmlVisitor;
import sdloader.javaee.webxml.WebXmlWriter;
import sdloader.javaee.webxml.WebXmlWritingContext;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;

public class WebXmlWriterImpl implements WebXmlWriter {

	protected WebXmlVisitor visitor;

	protected Writer writer;

	protected static SDLoaderLog LOG = SDLoaderLogFactory
			.getLog(WebXmlWriterImpl.class);

	public WebXmlWriterImpl(WebXmlVisitor visitor, Writer writer) {
		this.visitor = visitor;
		this.writer = writer;
	}

	public void write(WebXml webXml) {
		if (webXml == null) {
			throw new NullPointerException("web xml");
		}
		visitor.visit(webXml.getWebApp());
		final WebXmlWritingContext context = visitor.getContext();
		try {
			writer.write(context.getXml());
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				LOG.debug(e.getMessage());
			}
			context.clear();
		}
	}
}
