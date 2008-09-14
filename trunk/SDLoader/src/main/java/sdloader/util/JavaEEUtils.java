/*
 * Copyright 2005-2007 the original author or authors.
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
package sdloader.util;

import javax.servlet.http.HttpServletRequest;

import sdloader.javaee.JavaEEConstants;

/**
 * Utility for JavaEE.
 * @author c9katayama
 */
public class JavaEEUtils {

	public static boolean isForwardRequest(HttpServletRequest request){
		return (request.getAttribute(JavaEEConstants.JAVAX_FORWARD_SERVLET_PATH) != null);
	}
	public static boolean isIncludeRequest(HttpServletRequest request){
		return (request.getAttribute(JavaEEConstants.JAVAX_INCLUDE_SERVLET_PATH) != null);
	}
}
