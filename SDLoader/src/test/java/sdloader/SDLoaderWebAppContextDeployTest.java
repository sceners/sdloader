package sdloader;

import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderWebAppContextDeployTest {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setUseOutSidePort(true);
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
