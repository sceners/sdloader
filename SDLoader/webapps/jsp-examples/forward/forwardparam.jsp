<html>
PARAM<BR>
<%="getRequestURL=" + request.getRequestURL() %><br>
<%="getRequestURI=" + request.getRequestURI() %><br>
<%="getServletPath=" + request.getServletPath() %><br>
<%="getPathInfo=" + request.getPathInfo() %><br>
<%="javax.servlet.forward.request_uri="+request.getAttribute("javax.servlet.forward.request_uri") %><br>
<%="javax.servlet.forward.context_path="+request.getAttribute("javax.servlet.forward.context_path") %><br>
<%="javax.servlet.forward.servlet_path="+request.getAttribute("javax.servlet.forward.servlet_path") %><br>
<%="javax.servlet.forward.path_info="+request.getAttribute("javax.servlet.forward.path_info") %><br>
<%="javax.servlet.forward.query_string="+request.getAttribute("javax.servlet.forward.query_string") %><br>
</html>