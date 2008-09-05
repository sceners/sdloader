<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>T2 Sample</title>
</head>
<body>
<h1>T2 Sample</h1>
<ul>
	<li><a href="${t:url('/hello')}">Hello</a></li>
	<li><a href="${t:url('/add')}">足し算</a></li>
	<li><a href="${t:url('/select')}">プルダウン項目</a></li>
	<li><a href="${t:url('/checkbox')}">チェックボックス項目</a></li>
	<li><a href="${t:url('/twitter/publicTimeline')}">Twitter public timeline</a></li>
	<li><a href="${t:url('/getandpost')}">シンプルなGETとPOST</a></li>
	<li><a href="${t:url('/download')}">ファイルダウンロード</a></li>
	<li><a href="${t:url('/dialog')}">ダイアログ</a></li>
</ul>
</body>
</html>
