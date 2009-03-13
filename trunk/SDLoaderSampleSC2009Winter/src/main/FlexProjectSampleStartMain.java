package main;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class FlexProjectSampleStartMain {

	public static void main(String[] args) {

		// インスタンス化
		SDLoader loader = new SDLoader(8080);
		
		loader.setUseNoCacheMode(true);
		
		//loader.setLineSpeed(LineSpeed.ISDN_64K_BPS);
		
		// WebApp追加 ルートを複数指定
		loader.addWebAppContext(new WebAppContext("/sample", "../SDLoaderSampleSC2009Winter-Flex3/bin-debug","WebContent"));

		// 起動
		loader.start();
		
		Browser.open("http://localhost:"+loader.getPort()+"/sample/main.html");
	}
}
