/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
/* $Id: SessionExample.java,v 1.4 2004/08/26 22:03:27 markt Exp $
 *
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import util.HTMLFilter;

/**
 * Example servlet showing request headers
 *
 * @author James Duncan Davidson <duncan@eng.sun.com>
 */

public class SessionExample extends HttpServlet {

    ResourceBundle rb = ResourceBundle.getBundle("LocalStrings");
    
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body bgcolor=\"white\">");
        out.println("<head>");

        String title = rb.getString("sessions.title");
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body>");

        // Can't use relative links as the addition of pathinfo to the URI
        // breaks relative links. Therefore use absolute links but don't hard
        // code the context name so webapp can be deployed as different context
        // and will still work.
        String context = request.getContextPath();
        
        out.println("<a href=\"" + context + "/servlets/sessions.html\">");
        out.println("<img src=\"" + context + "/images/code.gif\" height=24 " +
                    "width=24 align=right border=0 alt=\"view code\"></a>");
        out.println("<a href=\"" + context + "/servlets/index.html\">");
        out.println("<img src=\"" + context + "/images/return.gif\" " +
                    "height=24 width=24 align=right border=0 alt=\"return\">" +
                    "</a>");

        out.println("<h3>" + title + "</h3>");

        HttpSession session = request.getSession();
        out.println(rb.getString("sessions.id") + " " + session.getId());
        out.println("<br>");
        out.println(rb.getString("sessions.created") + " ");
        out.println(new Date(session.getCreationTime()) + "<br>");
        out.println(rb.getString("sessions.lastaccessed") + " ");
        out.println(new Date(session.getLastAccessedTime()));

        String dataName = request.getParameter("dataname");
        String dataValue = request.getParameter("datavalue");
        if (dataName != null && dataValue != null) {
            session.setAttribute(dataName, dataValue);
        }

        out.println("<P>");
        out.println(rb.getString("sessions.data") + "<br>");
        Enumeration names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement(); 
            String value = session.getAttribute(name).toString();
            out.println(HTMLFilter.filter(name) + " = " 
                        + HTMLFilter.filter(value) + "<br>");
        }

        out.println("<P>");
        out.print("<form action=\"");
	out.print(response.encodeURL("SessionExample"));
        out.print("\" ");
        out.println("method=POST>");
        out.println(rb.getString("sessions.dataname"));
        out.println("<input type=text size=20 name=dataname>");
        out.println("<br>");
        out.println(rb.getString("sessions.datavalue"));
        out.println("<input type=text size=20 name=datavalue>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");

        out.println("<P>GET based form:<br>");
        out.print("<form action=\"");
	out.print(response.encodeURL("SessionExample"));
        out.print("\" ");
        out.println("method=GET>");
        out.println(rb.getString("sessions.dataname"));
        out.println("<input type=text size=20 name=dataname>");
        out.println("<br>");
        out.println(rb.getString("sessions.datavalue"));
        out.println("<input type=text size=20 name=datavalue>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");

        out.print("<p><a href=\"");
	out.print(response.encodeURL("SessionExample?dataname=foo&datavalue=bar"));
	out.println("\" >URL encoded </a>");
	
        out.println("</body>");
        out.println("</html>");
        
        out.println("</body>");
        out.println("</html>");
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }

}
