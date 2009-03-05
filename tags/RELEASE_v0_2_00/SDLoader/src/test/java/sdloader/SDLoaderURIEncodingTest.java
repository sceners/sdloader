package sdloader;

import junit.framework.TestCase;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderURIEncodingTest extends TestCase {

	public static void main(String[] args){
		SDLoader sdloader = new SDLoader(true);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/testwebapp","testwebapp");		
		sdloader.addWebAppContext(webapp);
		
		sdloader.start();
		try{
			MiscUtils.openBrowser("http://localhost:"+sdloader.getPort()+"/testwebapp/uriencodetest/%E5%86%86%E9%AB%98%E5%BA%A6.txt");
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
