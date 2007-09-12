<%@page contentType="text/xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<?xml version="1.0" encoding="UTF-8"?>
<list>
<%
java.io.File file = new java.io.File("c:/");//System.getProperty("user.home")+"/Favorites");
java.io.File[] files = file.listFiles();
%>
<%
for(int i = 0;i < files.length;i++){
	if(files[i].isDirectory()){
		out.write("<dir>");
		request.setAttribute("value",files[i].getName());
%>
		<c:out value="${value}" escapeXml="true"></c:out>
<%
		out.write("</dir>\n");
	}	
}
%>
<%
for(int i = 0;i < files.length;i++){
	if(files[i].isDirectory()){
		out.write("<file>");
		request.setAttribute("value",files[i].getName());
%>
		<c:out value="${value}" escapeXml="true"></c:out>
<%
		out.write("</file>\n");
	}	
}
%>

</list>