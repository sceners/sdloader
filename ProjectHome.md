http://f.hatena.ne.jp/images/fotolife/c/c9katayama/20080903/20080903204909.png?1220442611

# SDLoader #
SDLoaderは、シンプルなデスクトップサーブレットコンテナです。

Webアプリケーションの開発時や、ちょっとWebアプリを動かしたい時などに便利です。

デスクトップ用途のため、サーブレットの基本機能とJSPの機能のみ実装していますが、その分高速に起動します。

SDLoaderDesktopSWTを使用すると、webappsフォルダにwarファイルをコピーするだけで、exeからWebアプリを起動できます。

コピーするだけ動作し、USBからも起動できるので、デモ利用に最適です。

exe化には[exewrap](http://www.ne.jp/asahi/web/ryo/exewrap/)を使用しています。
# ダウンロード #
各アーカイブの説明です。
|アーカイブ名|用途|
|:-----------------|:-----|
|[sdloader-jsp21-v0\_3\_04.zip](http://sdloader.googlecode.com/files/sdloader-jsp21-v0_3_04.zip)|ライブラリアーカイブ。主に開発用途で利用します。|
|[sdloader-jsp21-standalone-v0\_3\_04.zip](http://sdloader.googlecode.com/files/sdloader-jsp21-standalone-v0_3_04.zip)|スタンドアロン実行用アーカイブ。warを置いて、ブラウザでアプリを起動したい場合に利用します。|
|[sdloader-src-v0\_3\_04.zip](http://sdloader.googlecode.com/files/sdloader-src-v0_3_04.zip)|SDLoader本体のソースコードです。|
|[SDLoaderDesktopSWT-v0\_0\_10.zip](http://sdloader.googlecode.com/files/SDLoaderDesktopSWT-v0_0_10.zip)|スタンドアロン実行用アーカイブ。ブラウザの代わりに、SWTの画面が起動します。|

なお、JSPがないもの、JSP20を使用するものは、Downloadsのタブの一覧からダウンロードできます。

## SDLoaderリリース ##
### v0\_3\_04 ###
  * ポート再利用のロジックを見直し
  * SDLoaderの停止コマンド実行時に正しく通信できるかを確認してから再利用するように修正
  * Open,SystemTrayOpen,BrowserOpenでコマンドライン引数を取れるように修正
  * 引数に--openBrowser=true or false で、SDLoader起動後にブラウザを開けるかどうかを決められるオプションを追加
  * getSession()していない時にセッションが作られていた不具合を修正


### 以前のリリースノート ###
  * [SDLoaderReleaseNote](http://code.google.com/p/sdloader/wiki/SDLoaderReleaseNote)

## SDLoaderDesktopSWTリリース ##
### v0\_0\_09 ###
  * SDLoaderをv0\_3\_03に変更
  * 最大化ボタンを使用可否を指定できるように修正(application.propertiesに window.maximizebutton=trueを指定）
  * 最大化状態での起動可否を指定できるように修正(application.propertiesにwindow.maximized=trueを指定）

### 以前のリリースノート ###
  * [SDLoaderDesktopSWTReleaseNote](http://code.google.com/p/sdloader/wiki/SDLoaderDesktopSWTReleaseNote)