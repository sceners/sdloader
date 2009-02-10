package sdloader;

import sdloader.javaee.WebAppContext;
import sdloader.javaee.webxml.FilterMappingTag;
import sdloader.javaee.webxml.FilterTag;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.ServletTag;
import sdloader.javaee.webxml.WebXml;
import sdloader.javaee.webxml.WelcomeFileListTag;
import sdloader.testwebapp.filteranddispatchtest.AllFilter;
import sdloader.testwebapp.filteranddispatchtest.ForwardFilter;
import sdloader.testwebapp.filteranddispatchtest.ForwardServlet;
import sdloader.testwebapp.filteranddispatchtest.IncludeFilter;
import sdloader.testwebapp.filteranddispatchtest.IncludeServlet;
import sdloader.testwebapp.filteranddispatchtest.RequestFilter;
import sdloader.testwebapp.filteranddispatchtest.RequestServlet;
import sdloader.util.MiscUtils;

public class TestWebXmlDeployWebAppServerStart {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		
		WebAppContext webapp = new WebAppContext("/testwebapp","testwebapp");
		WebXml webXml = new WebXml();
		webXml.getWebApp()
		.addFilter(new FilterTag().setFilterName("requestFilter").setFilterClass(RequestFilter.class))
		.addFilter(new FilterTag().setFilterName("includeFilter").setFilterClass(IncludeFilter.class))
		.addFilter(new FilterTag().setFilterName("forwardFilter").setFilterClass(ForwardFilter.class))
		.addFilter(new FilterTag().setFilterName("allFilter").setFilterClass(AllFilter.class.getName()))
		.addFilterMapping(new FilterMappingTag().setFilterName("requestFilter").setUrlPattern("/filterAndDispatchTest/*").addDispatcher("REQUEST"))		
		.addFilterMapping(new FilterMappingTag().setFilterName("includeFilter").setUrlPattern("/filterAndDispatchTest/*").addDispatcher("INCLUDE"))
		.addFilterMapping(new FilterMappingTag().setFilterName("forwardFilter").setUrlPattern("/filterAndDispatchTest/*").addDispatcher("FORWARD"))
		.addFilterMapping(new FilterMappingTag().setFilterName("allFilter").setUrlPattern("/filterAndDispatchTest/*").addDispatcher("REQUEST")
				.addDispatcher("INCLUDE").addDispatcher("FORWARD")	)
		.addServlet(new ServletTag().setServletName("requestServlet").setServletClass(RequestServlet.class))
		.addServlet(new ServletTag().setServletName("includeServlet").setServletClass(IncludeServlet.class))
		.addServlet(new ServletTag().setServletName("forwardServlet").setServletClass(ForwardServlet.class))
		.addServletMapping(new ServletMappingTag().setServletName("requestServlet").setUrlPattern("/filterAndDispatchTest/requestServlet"))
		.addServletMapping(new ServletMappingTag().setServletName("includeServlet").setUrlPattern("/filterAndDispatchTest/includeServlet"))
		.addServletMapping(new ServletMappingTag().setServletName("forwardServlet").setUrlPattern("/filterAndDispatchTest/forwardServlet"))
		.setWelcomeFileList(new WelcomeFileListTag().addWelcomeFile("index.html"));
		
		webapp.setWebXml(webXml);
		
		sdloader.addWebAppContext(webapp);
		
		sdloader.start();
		try{
			MiscUtils.openBrowser("http://localhost:"+sdloader.getPort());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
