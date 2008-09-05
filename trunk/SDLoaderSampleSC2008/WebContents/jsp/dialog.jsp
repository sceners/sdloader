<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>ダイアログ</title>
</head>
<body>
<form name="checkForm" action="${t:url('/dialog')}" method="post">
<table>
	<tr>
		<td>
			<span>ダイアログ</span>
		</td>
	</tr>
	<tr>
		<td>
			<input type="submit" name="dialog1" value="呼び出し１" />
		</td>
	</tr>
	<br>
	<tr>
		<td>
			<input type="submit" name="dialog2" value="呼び出し２" />
		</td>
	</tr>
	<br>
	<tr>
		<td>
			<input type="submit" name="dialog3" value="呼び出し３" />
		</td>
	</tr>

	<tr>
		<td>
			${result}
		</td>
	</tr>
</table>
</form>
</body>
</html>
