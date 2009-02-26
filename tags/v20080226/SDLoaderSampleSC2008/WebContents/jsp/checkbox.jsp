<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" errorPage="/error/debug.jsp"%>
<%@taglib prefix="t" uri="http://www.t2framework.org/web/t2/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Checkbox チェックボックス項目</title>
</head>
<body>
<form name="checkForm" action="${t:url('/checkbox')}" method="post">
<table>
	<tr>
		<td>
			<span>Check:</span>
		</td>
		<td>
			<input type="checkbox" name="check"/>
		</td>
	</tr>
	<tr>
		<td>
			<span>${value == true ? 'YES' : 'NO'}</span>
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
