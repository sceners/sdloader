<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Select プルダウン項目</title>
</head>
<body>
<form name="selectForm" action="${t:url('/select')}" method="post">
<table>
	<tr>
		<td>
			<select name="moge">
				<option value="0">aaa</option>
				<option value="1">bbb</option>
				<option value="2">ccc</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>
			<span>${target}</span>
		</td>
	</tr>
	<tr>
		<td>
			<input type="submit" name="submit" value="SUBMIT" />
		</td>
	</tr>
</table>
</form>
</body>
</html>
