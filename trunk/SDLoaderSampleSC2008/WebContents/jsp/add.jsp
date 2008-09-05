<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>足し算画面</title>
</head>
<body>
<div>${message}</div>
<form name="addForm" action="${t:url('/add')}" method="post">
	<input type="text" name="arg1" value="${arg1}"/>
	<br />
	<input type="text" name="arg2" value="${arg2}"/>
	<br />
	<span>${result}</span><br />
	<input type="submit" name="add" value="同一ページ"/>
	<input type="submit" name="addAndMove" value="結果ページ"/>
</form>
</body>
</html>
