import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;


public class ServerStart {

	public static void main(String[] args) {
		
		SDLoader sdLoader = new SDLoader();
		//毎回空きポートを獲得する設定
		sdLoader.setAutoPortDetect(true);
		//コンテキストルートと、WEB-INFの入っているフォルダを指定
		WebAppContext context = new WebAppContext("/sample","WebContents");
		//SDLoaderに追加
		sdLoader.addWebAppContext(context);
		//SDLoaderスタート		
		sdLoader.start();
		//ブラウザもあける
		Browser.open("http://localhost:"+sdLoader.getPort()+"/sample/index.jsp");
	}

}
