package main;

import java.net.Inet4Address;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class PortSampleStartMain {

	public static void main(String[] args) throws Exception{

		// インスタンス化
		SDLoader loader = new SDLoader(8080);
		
		//自動ポート探知を使用
		loader.setAutoPortDetect(true);
		
		//外部ポートを使用
		loader.setUseOutSidePort(true);
		
		// WebApp追加
		loader.addWebAppContext(new WebAppContext("/sample", "WebContent"));

		// 起動
		loader.start();
		
		int port = loader.getPort();
		String ip = Inet4Address.getLocalHost().getHostAddress();
		Browser.open("http://" + ip +":"+port+"/sample/index.html");
	}
}
