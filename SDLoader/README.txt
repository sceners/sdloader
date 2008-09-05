SDLoader

[はじめに]
SDLoaderは、シンプルなサーブレットコンテナです。
ServletAPI2.4準拠を目指していますが、大部分が未実装です。
サーブレット・JSPを動かす最低限の実装しかありません。

standaloneフォルダ下に,３つのフォルダが提供されています。内容は次のとおりです。
sdloader・・・Servletのみ動作
sdloader-jsp12・・・ServletとJSP1.2
sdloader-jsp20・・・ServletとJSP2.0

[使い方]
jarファイルと同じ場所に、「webapps」フォルダを作成します。
webappsフォルダに、warファイル、ディレクトリ、コンテキストXMLのいずれかを配置します。

・warファイル
warファイルは、ファイル名がコンテキストパスになり、またwarファイル展開後の
フォルダがドキュメントルートになります。
例えばsample.warを配置し、中にindex.jspが入っているとすると、
http://localhost:30000/sample/index.jsp
でアクセスすることが出来ます。

・ディレクトリ
ディレクトリは、そのディレクトリがコンテキストパスになり、またドキュメントルートに
なります。
例えばsampleディレクトリを作成し、中にindex.jspが入っているとすると、
http://localhost:30000/sample/index.jsp
でアクセスすることが出来ます。

・コンテキストXML
<Context path="/example" docBase="../../example/webContents"/>
のような形で記述したXMLファイルを配置すると、そのファイルに従ってコンテキストパス
およびドキュメントルートを設定できます。
pathはコンテキストパスの設定で、docBaseがドキュメントルートになります。
path省略可能で、省略すると、このファイル名がコンテキストパスとなります。
docBaseは、.(ドット）表記からはじめるとwebappsフォルダからの相対パス、それ以外は絶対パスとなります。


[eclipseで利用する場合]
eclipseのプロジェクトに組み込んで使う場合、コンテキストルートをSDLoaderに指示してあげる
必要があります。

eclipseのプロジェクトが、このような構成になっていると仮定します。

example（プロジェクトフォルダ）
  |--sdloader-jsp20.jar
  |--webapps
  |    |--example.xml
  |--WebContents
       |--WEB-INF
       |     |--web.xml
       |--test.jsp


A,プログラマチックな方法
はじめに、sdloaderのjarにパスに通します。
次に、このようなmainメソッドを持ったクラスを実装します。
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
		//ブラウザをあける
		Browser.open("http://localhost:"+sdLoader.getPort()+"/sample/index.jsp");
	}
}

WebAppContextというのが、コンテキストパスと、WEB-INFの入っているフォルダの指定になります。
このクラスを実行すると、SDLoaderが起動し、アプリがデプロイされた後、ブラウザが開きます。
ソースフォルダ内にmainメソッドを持ったクラスが入るため、一番分かりやすい方法です。


B,設定ファイルを使う方法
はじめに、sdloaderのjarにパスに通します。
プロジェクト直下に、webappsという名前のフォルダを作成します。

フォルダ内に、「コンテキストパス名.xml」という名前のファイルを作成し、次のように記述します。
<Context docBase="../webContents"/>
docBaseは、このxmlファイルからの相対パスで記述します。

この状態で、sdloaderのJarの中の「sdloader.BrowserOpen」クラスを右クリック実行もしくはデバック実行すると、
アプリケーションが動作します。
このやり方の場合、ソースフォルダに不要なクラスが入らないという利点があります。

