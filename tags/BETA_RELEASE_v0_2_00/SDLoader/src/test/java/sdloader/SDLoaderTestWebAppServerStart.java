package sdloader;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderTestWebAppServerStart {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		
		WebAppContext webapp = new WebAppContext("/testwebapp","testwebapp");
		
		sdloader.addWebAppContext(webapp);
		
		sdloader.start();
		try{
			MiscUtils.openBrowser("http://localhost:"+sdloader.getPort());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
