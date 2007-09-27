・構成
build.xmlでビルドすると、以下のファイルが作成されます。
実行に必要なのは以下のファイル/フォルダです。

WebAppStandaloneKit.exe・・・実行ファイル
WebAppStandaloneKit.jar・・・実行ファイルに入っているjarファイル（このファイルは起動時には）
application.properties・・・アプリケーションの設定ファイル。webアプリを呼び出す為のボタンを作成したりできます。
webapps・・・この中にwarファイルを入れると、実行時に解凍されデプロイされます。
lib・・・実行用のlibファイル　SDLoader（WebApサーバ）が入っています。


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
JSP1.2で動作させたい場合で実行環境がJREの場合は、JDKから${JDK_HOME}/lib/tools.jar
というファイルを持ってきて、libフォルダもしくはjre/libに入れて下さい。