<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
	errorPage="/error/debug.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Twitter public timeline</title>
</head>
<body>
<table border="1">
	<c:forEach var="e" items="${timeline}" varStatus="s">
		<tr>
			<td><span>${e.text}</span></td>
			<td><span>${e.user.name}</span></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
