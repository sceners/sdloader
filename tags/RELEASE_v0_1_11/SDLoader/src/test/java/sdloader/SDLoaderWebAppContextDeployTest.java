package sdloader;

import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderWebAppContextDeployTest {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		WebAppContext webapp = new WebAppContext("/ex","webapps/examples");
		sdloader.addWebAppContext(webapp);
		
		sdloader.start();
		try{
			MiscUtils.openBrowser("http://localhost:"+sdloader.getPort());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
