## コマンドラインオプション ##
CommendLineOpenクラスの引数一覧です。
```
java -cp sdloader.jar sdloader.CommandLineOpen オプション
```

すべてのオプションは、「--option=value」 の形で記述します。

--port             Listenするポート番号

> 指定しない場合30000を使用します。

> 例）--port=8080

--webApps          デプロイするアプリケーションのwarファイル、もしくはディレクトリを指定します。

> 「;」で区切ると、複数のアプリケーションをデプロイできます。

> コンテキストルートには、warファイル名がもしくはディレクトリ名が使用されます。

> 例）--webApps=/path/to/app.war

--home             SDLoaderホームディレクトリを指定します。

> このディレクトリ下の「webapps」もしくは--webAppsDirオプションで指定したディレクトリにアプリケーションを配置します。

> 指定しない場合、Java実行ディレクトリを使用します。

> 例）--home=/path/to/sdloader

--webAppsDir       アプリケーションの入ったディレクトリを指定します。

> 相対パスの場合、ホームディレクトリからの相対パスになります。

> 指定しない場合、「webapps」が使用されます。

> 例）--webAppsDir=weblibs

--autoPortDetect   指定ポートが使用中の場合、空きポートを探すかどうか。

> 指定しない場合、falseが使用されます。

> 例）--autoPortDetect=true

--useOutSidePort   外部からのアクセス可能なポートを使用するかどうか。

> 指定しない場合、localhostのみのリクエストを受け付けます。

> 例）--useOutSidePort=true

--sslEnable        SSLを使用するかどうか。

> 指定しない場合、SSLは使用しません。

> 例）--sslEnable=true

--lineSpeed        擬似的な回線速度を設定します。

> 指定しない場合、速度制限はありません。

> 例）--lineSpeed=64000 (64Kbpsの場合)

--workDir          JSPおよびwarファイル展開用のworkディレクトリを指定します。

> 指定しない場合、${java.io.tmp}/.sdloaderを使用します。

> 例）--workDir=/path/to/dir

--openBrowser      SDLoader起動後にブラウザを開くかどうかを指定します。

> 例）--openBrowser=true

--waitForStop      SDLoaderがシャットダウンするまで、mainメソッドでwaitするかどうか。

> 例）--waitForStop=true

なお、この一覧は--helpでも出力できます。