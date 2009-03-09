package sdloader;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class TestWebAppTest extends TestCase{

	private SDLoader sdloader;
	@Override
	protected void setUp() throws Exception {
		sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		
		WebAppContext webapp = new WebAppContext("/testwebapp","webapps/test");
		
		sdloader.addWebAppContext(webapp);
		
		sdloader.start();
		try{
			MiscUtils.openBrowser("http://localhost:"+sdloader.getPort());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		sdloader.stop();
	}
	
	public void testLoadOnStartUp(){
		
		assertEquals(5,StartUpList.startUpList.size());
		assertEquals("Servlet2", StartUpList.startUpList.get(0));
		assertEquals("Servlet5", StartUpList.startUpList.get(1));
		assertEquals("Servlet3", StartUpList.startUpList.get(2));
		assertEquals("Servlet1", StartUpList.startUpList.get(3));
		assertEquals("Servlet4", StartUpList.startUpList.get(4));
		
	}
}
