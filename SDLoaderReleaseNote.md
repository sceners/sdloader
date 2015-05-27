### v0\_3\_03 ###
  * pom.xmlが他プロジェクトで使用するとエラーになっていたのを修正
  * ServletとFilterの初期パラメータが空文字の場合にnullをセットしていたのを修正
  * ServletContext#getResourcePaths()で正しいパスが返っていなかったのを修正

### v0\_3\_02 ###
  * SystemTrayOpenで起動した場合に、停止しない問題を修正
  * パスが../から始まる場合、WebAppClassLoader#getResources()でリソースを重複して返していた問題を修正.
  * stop()時にSystem.exitを呼ぶためのsetExitOnStop()を追加.
  * mime.xmlを廃止（クラスに直書き。ただしファイルの読み込み自体は行い、読めたら上書き）
  * sdloader.propertiesのデフォルト設定をSDLoaderクラスに移動。ファイルが無くてもエラーにならないようにした

### v0\_3\_01 ###
  * ClassLoaderHandlerのhandleResourecesが正しく動作するように修正.
  * バインドする対象を、localhostから127.0.0.1に変更
  * 同一プロセス内で同じポートを再度バインドする際、REUSE\_ADDRで開くように修正.
  * 定数定義のクラスをconstantsに移動.
  * HttpServletRequest#getRequestURL()の戻り値が、ローカルのホスト名だったのを修正

### v0\_3\_00 ###
  * 起動時のパフォーマンスを向上
  * SSL時にnocacheを使わないようにした（ダウンロードできないコンテンツがあるため）
  * SDLoader#stop()時にエラーが出るときがある問題を修正(動作中のSocketProcessorが止まっていなかったため）
  * WebAppClassLoaderに介入するClassLoaderHandlerを追加。
  * WebAppContextに、addClassPathとaddLibPathを追加。
  * 同一プロセスで複数インスタンスを起動/停止するような利用方法に対応するよう修正。
  * autoPortDetectでない場合に使用ポートがふさがっていた時に、同ポートに停止コマンドを送るように修正。
  * 初期スレッド数をデフォルト2に変更

### v0\_2\_03 ###
  * サイズの大きいリクエスト/レスポンスの場合に、OutOfMemoryになっていた問題を修正。
  * Include時、ヘッダーなどの各種項目設定を無視するように修正。
  * 複数回setContentLengthを呼ぶとヘッダーが複数出ていたのを修正。

### v0\_2\_02 ###
  * コマンドライン引数をとるMainクラス(sdloader.CommandLineOpen)を追加。（詳細は[CommandLineオブション](http://code.google.com/p/sdloader/wiki/CommandLineOption)から確認できます）

  * URIEncodeがISO-8859-1の場合、デコード処理を行わないように変更。
  * getRealPath()がURL形式になっていたため、URIに変換するように修正した。（OS準拠のパスが取れるように変更）
  * SSLを使用可能にした。
  * WebXMLWriterを導入した。(WebXMLクラスをXMLにシリアライズできます）
  * 帯域制限なしの時に、通信が遅くなっていたのを修正
  * フォワード時にURLに付与したパラメータがフォワード先でとれなかったため、取れるように修正した。(T2のProceed対応）

### v0\_2\_01 ###
  * loadonstartupを実装
> (同じ数値の場合、タグ順に初期化。0以下もしくはタグなしの場合、loadonstartupタグありのServlet初期化後に、タグ順に初期化。
> > タグがない場合でも初回リクエストではなくタグ読み込み時に初期化します）

> ServletRequestListener,ServletRequestAttributeListenerを実装

  * 最大プール数以上のSocketProcessorを生成した場合、スレッド開始前に処理メソッドが呼ばれていた問題を修正
> （FireFoxなど通信数の多いブラウザで通信が失敗する不具合の修正）

  * ServletContext#getMajorVersionとgetMinorVersionを実装

  * ログ出力をJDKLoggerに１本化。

  * コマンドサーブレットをデプロイするようにした。
> ブラウザからhttp://localhost:port/sdloader-command/stopにアクセスするとSDLoaderが停止します。

  * WebAppContextにwarファイルパスを書けるようにした。
> new WebAppContext("/hoge","c:/foo.war")のように記述可能。

### v0\_2\_00 ###
  * ServletAPI2.5,JSP2.1に対応。
> これに伴い、sdloader.jarに同梱するapiを2.5に変更。
> JSP1.2を削除

  * ServletContextImplにgetContextPath追加（API変更による追加）
> InMemoryEmbeddedServletOptionに、Tomcat6のServletOptionの機能を追加

  * WebAppContextにWebXmlを設定可能にした。
> （WebAppContextにWebXmlをセットしておくと、それが優先されます）

  * LineSpeed機能を追加。
> SDLoader#setLineSpeed()にLineSpeedの定数をセットすると、
> 回線速度をエミュレートするようになります。

  * プロジェクトのフォルダ雛形を作る機能を追加。
> Servlet25ProjectTemplatToolおよびServlet24ProjectTemplatToolを
> 実行すると、その場にテンプレートが出力されます。

  * ServletContextAttributeListener,HttpSessionBindingListener,HttpSessionListenerに対応。

### v0\_1\_14 ###
  * WebAppContextを使用する際、複数のドキュメントルートを指定できるように修正。
> > 例えば、
```
   SDLoader loader = new SDLoader(8080);
   loader.addWebAppContext(new WebAppContext("/flex","bin-debug","WebContent"));
```
> > とすると、bin-debugをチェックし、ファイルがあればそこから、なければWebContentからファイルを探します。（Flex開発用の為）

  * HttpServletRequestからパスを取る部分をURIDecoderでコードするように修正。またデコードする時の文字コードを、loader.setURIEncoding("UTF-8");　のようにセットできるようにしました。（日本語の入ったURLに対応するため）
  * loader.setUseNoCacheMode(true)とすると、すべてのリクエストにno-cacheヘッダーを付与する機能を追加（アプレットやFlash開発時のキャッシュよけのため）。
  * FireFoxでリダイレクト時に動作が遅くなる現象を修正
  * その他クラス名の統一やリファクタリング
### v0\_1\_12 ###
  * URLClassLoader経由でSDLoaderを動かすとエラーになる不具合に対応。
  * (Webコンテナのライブラリをロードするクラスローダの親にSystemClassLoaderを指定していたため、
  * SDLoader.class.getClassLoader()を使用するように修正）
  * web.xmlのfilter-mapping（REQUEST,INCLUDE,FORWARD)に対応
  * lifecycle系のインターフェースをsdloader.lifecycleパッケージに移動
### v0\_1\_11 ###
  * SWTを使い、タスクトレーにアイコンをしまうようにした。
  * RequestDispatcherで、引数パスの先頭に/がついていない場合でも/をつけるように修正。
### v0\_1\_10 ###
  * WebAppContextクラスをnewし、SDLoaderにaddすることで、Webアプリをデプロイできるようにした。
  * 例えば次のようなコードをmainメソッドに書き実行すると
  * SDLoader sdloader = new SDLoader(8080);
  * WebAppContext webapp = new WebAppContext("/hoge","webapps/hogeapp");
  * sdloader.addWebAppContext(webapp);
  * http://localhost:8080/hoge に対して、webapps/hogeapp以下のファイルがデプロイされます。
### v0\_1\_09 ###
  * メンテナンスリリース
### v0\_1\_08 ###
  * パスに空白が入っている場合、動作しない問題を修正。
### v0\_1\_07 ###
  * sendRedirectで307を返していたので、302を返すように修正。
  * リクエストパラメータをデコードする際、デフォルトでISO-8859-1を使用し、setCharacterEncodingでエンコードを指定されて場合は指定されたものを使用するように修正。
  * コンテキストルートのみのURLを/付きでリダイレクトする場合に、host部分が127.0.0.1になっていたのを修正。リクエストのホスト部を使用するようにした。
  * デフォルトエンコーディングをGETパラメータにbodyエンコーディングを使うかどうかを設定できるようにした。
  * レスポンスのデフォルトエンコーディングをISO-8859-1に変更し、setContentTypeとsetCharacterEncodingでセットしたエンコードを文字エンコードに使用するようにした。
  * レスポンスのTransfer-EncodingがChunkedで無い場合、Content-LengthをSDLoaderでセットするようにした。
  * 外向けポートをListen出来るようにした。request.usetOutSidePortで設定可能。
### v0\_1\_06 ###
  * Context.xmlにシステムプロパティを使用できるようにした。${name}の形式でdocBaseを記述すると、nameをキーにしてSystemからプロパティを取得します。
  * PathPairクラスを外に出し、WebAppContextに名称変更。パース部分もWebAppManagerから分離。
### v0\_1\_05 ###
  * 無解凍war機能を追加（Servlet動作のみ）

> これに伴い、クラスローダーの構成を変更。
> 各Webアプリは、WebAppClassLoaderを持ちます。このクラスローダーは内部にweb-inf下をロードするクラスローダを持つ構造になっています。
> WebアプリはWebAppClassLoaderを通じてクラスロードを行いますが、loadClass時にWebAppClassLoaderはまずロード済みのクラスが無いかどうかを親クラスローダーに確認します。無い場合で指定のパッケージパターンを持つクラスの場合は、web-inf下をロードするクラスローダーでロードします。さらに無い場合は親クラスローダーでロードし、最後はweb-inf下のクラスローダーでロードします。
### v0\_1\_04 ###
  * イベントディスパッチャー追加。ライフサイクルにあわせてSDLoaderの起動・停止イベントを取れるようにした。
### v0\_1\_03 ###
  * ルート直下のファイルを扱うための機能を追加。 webapps直下のROOTフォルダにファイルを置くと、http://localhost/ファイル名でアクセスできます。
  * コマンド機能の追加。SDLoader起動と同時に、ポート8089をコマンド受付用としてバインドします。telnetもしくはHTTPで「stop」「restar」のコマンドを送ると、SDLoaderをコントロールできます。
### v0\_1\_02 ###
  * Webアプリの一覧表示機能を実装。
  * 共有セッション機能を実装。
  * 起動時にブラウザをオープンするクラスを実装
### v0\_1\_01 ###
  * welcome-file機能を実装。
### v0\_1\_00 ###
  * 最初のリリース