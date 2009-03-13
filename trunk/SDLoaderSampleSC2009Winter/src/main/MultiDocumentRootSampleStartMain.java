package main;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class MultiDocumentRootSampleStartMain {

	public static void main(String[] args) {

		// インスタンス化
		SDLoader loader = new SDLoader(8080);

		//自動ポート探知を使用
		loader.setAutoPortDetect(true);
		
		// WebApp追加 ルートを複数指定
		loader.addWebAppContext(new WebAppContext("/sample", "WebContent2","WebContent"));

		// 起動
		loader.start();
		
		Browser.open("http://localhost:"+loader.getPort()+"/sample/index.html");
	}
}
