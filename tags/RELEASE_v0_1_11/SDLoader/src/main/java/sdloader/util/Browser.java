package sdloader.util;

import java.io.IOException;

public class Browser {

	public static void open(String url){
		try{
			MiscUtils.openBrowser(url);
		}catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}
}
