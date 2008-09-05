<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Simple GET and POST</title>
</head>
<body>
<span>${ message }</span>
<form name="form1" action="${t:url('/getandpost')}" method="post">
	<input type="submit" name="posts" value="POST"/>
</form>
<form name="form2" action="${t:url('/getandpost')}" method="get">
	<input type="submit" name="gets" value="GET"/>
</form>
</body>
</html>
