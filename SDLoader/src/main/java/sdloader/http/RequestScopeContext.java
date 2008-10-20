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
package sdloader.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author c9katayama
 */
public class RequestScopeContext {

	private static ThreadLocal<RequestScopeContext> threadLocal = new ThreadLocal<RequestScopeContext>();
	
	private Map<Object,Object> attribute;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private RequestScopeContext(){		
	}
	public static RequestScopeContext getContext(){
		RequestScopeContext context = threadLocal.get();
		if(context==null){
			init();
			context = threadLocal.get();
		}
		return context;
	}
	public static void init(){
		threadLocal.set(new RequestScopeContext());
	}
	public static void destroy(){
		threadLocal.remove();
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public HttpServletResponse getResponse() {
		return response;
	}	
	public void setAttribute(Object key,Object value){
		if(attribute==null){
			attribute = new HashMap<Object, Object>();
		}
		attribute.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(Object key){
		if(attribute==null){
			return null;
		}
		return (T)attribute.get(key);
	}
}
