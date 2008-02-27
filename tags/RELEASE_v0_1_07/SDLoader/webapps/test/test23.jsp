<%@page contentType="text/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<html>
<head><title>JSP Page</title></head>
<body>
<h1>c:out</h1>
test:<c:out value="writing"/><br>
<br>
<%
request.setAttribute("sss","t<u>es</u>t");
%>
<%="RemoteAddr=" + request.getRemoteAddr() %><br>
<%="RemoteHost=" + request.getRemoteHost() %><br>
<%="ServerName=" + request.getServerName() %><br>
<%="ServerPort=" + request.getServerPort() %><br>
expression:<%=request.getAttribute("sss")%><br>
tag:<c:out value="${sss}"/><br>
tag(not escape):<c:out value="${sss}" escapeXml="false"/><br>
</body>
</html>

