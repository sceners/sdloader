<%@page language="java" contentType="text/html"%>
<html>
<head><title>JSP Page</title></head>
<body>
<h1>c:out</h1>
test:${writing}<br>
<br>
<%
request.setAttribute("sss","t<u>es</u>t");
%>
<%="RemoteAddr=" + request.getRemoteAddr() %><br>
<%="RemoteHost=" + request.getRemoteHost() %><br>
<%="ServerName=" + request.getServerName() %><br>
<%="ServerPort=" + request.getServerPort() %><br>
expression:<%=request.getAttribute("sss")%><br>
tag:${requestScope.sss}<br>
tag(not escape):${sss}<br>
</body>
</html>

