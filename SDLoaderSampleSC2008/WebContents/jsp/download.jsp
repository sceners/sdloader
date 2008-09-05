<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>ファイルダウンロード画面</title>
</head>
<body>
<form name="downloadForm" action="${t:url('/download')}" method="get">
	<input type="text" name="filename" />
	<br />
	<span>${result}</span><br />
	<input type="submit" name="simpleDownloadByGet" value="GETでファイルダウンロード"/>
	<hr />
	<span>以下のボタンはファイルが見つからないときのエラーハンドリングのテスト用ボタンです.</span><br />
	<input type="submit" name="errorByResourceNotFound" value="エラーボタン"/>
</form>
</body>
</html>
