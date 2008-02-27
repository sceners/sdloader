・構成
build.xmlでビルドすると、以下のファイルが作成されます。
実行に必要なのは以下のファイル/フォルダです。

SDLoaderDesktop.exe・・・実行ファイル
SDLoaderDesktop.jar・・・実行ファイルに入っているjarファイル（このファイルは起動時には不要です）
application.properties・・・アプリケーションの設定ファイル。起動設定や、webアプリケーション実行の設定を行います。
webapps・・・この中にwarファイルを入れると、実行時に解凍されデプロイされます。
lib・・・実行用のlibファイル　SDLoader（WebApサーバ）が入っています。

実行ファイルは、デフォルトでは2重起動禁止になっています。
これを変更したい場合は、build.xml中の
	<exec executable="./exewrap/exewrap.exe">
		<arg line="-g -e SINGLE ${dist}/SDLoaderDesktop.jar"/>  	
	</exec>
の部分のSINGLEを取り外して、再ビルドして下さい。

・構成方法
まず、webappsにwarファイルをおきます。
次に、application.propertiesを設定します。
設定は、application.propertiesを見て下さい。

・JREを同梱したい場合
exeと同じレベルのフォルダに、「jre」の名前でフォルダを作り、JDKもしくはJREを入れておくと、
OSにjreをインストールしていないPCでも動作するようになります。
JDK/JREは1.5以上を入れて下さい。

・JSPを動かす場合
sdloader-jsp20.jarがlibに入っている場合はそのまま動作します（JSP2.0レベルで動作します）
JSP1.2で動作させたい場合で実行環境がJREの場合は、sdloader-jsp20.jarをsdloder-jsp12.jarに
変更し、JDKから${JDK_HOME}/lib/tools.jarというファイルを持ってきて、libフォルダもしくはjre/libに入れて下さい。