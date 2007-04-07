<!--
  Copyright 2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<%="getRequestURL=" + request.getRequestURL() %><br>
<%="getRequestURI=" + request.getRequestURI() %><br>
<%="getServletPath=" + request.getServletPath() %><br>
<%="getPathInfo=" + request.getPathInfo() %><br>
<%="javax.servlet.include.request_uri="+request.getAttribute("javax.servlet.include.request_uri") %><br>
<%="javax.servlet.include.context_path="+request.getAttribute("javax.servlet.include.context_path") %><br>
<%="javax.servlet.include.servlet_path="+request.getAttribute("javax.servlet.include.servlet_path") %><br>
<%="javax.servlet.include.path_info="+request.getAttribute("javax.servlet.include.path_info") %><br>
<%="javax.servlet.include.query_string="+request.getAttribute("javax.servlet.include.query_string") %><br>
