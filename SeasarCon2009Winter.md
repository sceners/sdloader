#SeasarCon2009Winter用草案

# SDLoaderとは #
**元々はFlash-Javaのアプリケーションデモ用に作成** 営業や研修用に利用したい
**利用者になるべく楽に渡したい
  1. デモアプリがたくさんあるため、各々設定が面倒
  1. 極力渡すファイルを少なくしたい（jarとwarだけとか）
  1. ノートPCがしょぼいため、極力軽くて早く起動するものがほしい** TomcatやWinston
  1. Tomcatほど機能は要らない
  1. Winstonもよかったけど、ライセンスがGPLだった
  1. ServletAPIを実装してみたかった
**現在
  1. JSP（Jasperですけど）
  1. 開発に便利な機能
  1. SWTとの連携
  1. JDK5
  1. Apache2.0 License**

# 特徴 #
**Servletのみの最小jarが300K
  1. jspありだと3M** 必要なのはJar1個
  1. jasperやswtも入れてしまってます(汗
**自動ポート検知機能
  1. 指定したポートが利用中だったら、ほかのポートを探す** デフォルトでローカルホストのみをListen
  1. 外からの接続ができない代わりに、Windowsやノートン先生の警告が出ない
**newするWebコンテナ
  1. new SDLoader()でサーバインスタンス作成
  1. コーディングベースで、コンテキストルート、ドキュメントルート、web.xmlなどを指定可能。** マルチドキュメントルート
  1. 複数個所からクラスやリソースをロードできます
**NoCache機能
  1. ONにすると、すべてのレスポンスに対してNoCacheヘッダーを入れまます。** 帯域制限
  1. LineSpeed指定をすれば、擬似的に回線速度を落とせます。

# 開発に利用 #
**基本的な流れ
  1. Javaプロジェクト作成
  1. sdloader-jsp21.jarをコピー
  1. クラスパスに登録
  1. jar右クリック->実行->アプリケーション->Servle25ProjectTemplateTool
  1. SDLoaderServerStartで開始
  1. 止めるときはアプリ停止**

# スタンドアロンで利用 #
**基本的な流れ
  1. webappsフォルダにwarを配置
  1. sdloaderstart-jsp21.batを実行で開始
  1. 止めるときはタスクトレーから**

# ケースその１ デモアプリ作成依頼 #
**SpringとiBatisのサンプル作ってほしい** TomcatPlugin？WTP？WebLauncher？バージョンは？
**デモ用設定を自分のTomcatに入れたくない** チェックアウトしてmainメソッド実行ですぐ動く
**起動するURLを指定しておけば、ブラウザが立ち上がる**

# ケースその２ 試しにWebアプリを作りたい #
**Webフレームワークの評価や、ちょっとしたアプリ** 作ったけど途中で終わった
**消すのももったいないけど、ワークスペースからは消したい** ある程度動く状態で残したいけど、次やるときに環境設定したくない

# ケースその３ 実案件 #
**デバック用にフィルターをつけたいなど、web.xmlが複数個ほしいケース**

# Flex開発利用 #
**JavaプロジェクトとFlexプロジェクトを分ける場合の問題
  1. マルチドキュメントルートが便利
  1. プログレスバーなどを実装に、帯域制限が便利**

# デモ利用 #
USBに入れて

# スタンドアロンの実案件例 #
**本番アプリの研修用に、スタンドアロンで利用（Flash-Java,JSP-Java)** タブレットPCに入れ、スタンドアロンで利用(Flex3-Java)
**EclipseRCPで作成された顧客管理アプリに、FlexのUIを適用。
> FlexからServletを呼び出し、そこからRCP内のサービスを呼び出し(Flex2-Java)** 展示用にスタンドアロンで稼動(Flash-Java)
**ケースとしては、すでにJavaのビジネスロジックがあり、FlashやFlexで画面を
> 作ってスタンドアロンで使いたいケース**


# ServletAPI実装状況 #
**基本的にはServletAPI2.4** @Resourceなどのアノテーション系は未実装
**ServletContext
  1. getNamedDispatcher以外は実装** HttpServlet
**Filter** HttpServletRequest
  1. getUserPrincipal,isUserInRoleは未実装
**HttpSession
  1. HttpSessionActivationListenerは未実装** HttpServletResponse

# web.xml実装状況 #


&lt;context-param&gt;




&lt;filter&gt;

,

&lt;filter-mapping&gt;




&lt;listener&gt;




&lt;servlet&gt;

,

&lt;servlet-mapping&gt;



&lt;load-on-startup&gt;




&lt;jsp-file&gt;

(Jasperがんばる）
  1. 

&lt;security-role-ref&gt;

は未実装


&lt;welcome-file-list&gt;




&lt;taglib&gt;





&lt;session-config&gt;

は未実装
  1. タイムアウトの実装予定なし



&lt;env-entry&gt;

,

&lt;resource-ref&gt;


  1. 次に導入予定

# 今後 #
JNDI
開発支援系