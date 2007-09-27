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
package sdloader.j2ee.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import sdloader.util.IteratorEnumeration;

/**
 * HttpSession実装クラス
 * 
 * @author c9katayama
 */
public class HttpSessionImpl implements HttpSession {
	private String id;

	private long creationTime;

	private long lastAccessedTime;

	private ServletContext servletContext;

	private int maxInactiveInterval;

	private Map attributeMap = new HashMap();

	private boolean invalidate = false;

	private boolean isNew = true;

	public HttpSessionImpl(String sessionId) {
		this.id = sessionId;
		this.creationTime = System.currentTimeMillis();
	}

	public long getCreationTime() {
		checkInvalidate();
		return creationTime;
	}

	public String getId() {
		checkInvalidate();
		return id;
	}

	public long getLastAccessedTime() {
		checkInvalidate();
		return lastAccessedTime;
	}

	public ServletContext getServletContext() {
		checkInvalidate();
		return servletContext;
	}

	public void setMaxInactiveInterval(int interval) {
		checkInvalidate();
		this.maxInactiveInterval = interval;
	}

	public int getMaxInactiveInterval() {
		checkInvalidate();
		return maxInactiveInterval;
	}

	public HttpSessionContext getSessionContext() {
		throw new RuntimeException("getSessionConetxt not implemented");
	}

	public Object getAttribute(String key) {
		checkInvalidate();
		return attributeMap.get(key);
	}

	public Object getValue(String key) {
		checkInvalidate();
		return getAttribute(key);
	}

	public Enumeration getAttributeNames() {
		checkInvalidate();
		return new IteratorEnumeration(attributeMap.keySet().iterator());
	}

	public String[] getValueNames() {
		checkInvalidate();
		return (String[]) attributeMap.keySet().toArray(new String[] {});
	}

	public void setAttribute(String key, Object value) {
		checkInvalidate();
		attributeMap.put(key, value);
	}

	public void putValue(String key, Object value) {
		checkInvalidate();
		setAttribute(key, value);
	}

	public void removeAttribute(String key) {
		checkInvalidate();
		attributeMap.remove(key);
	}

	public void removeValue(String key) {
		checkInvalidate();
		removeAttribute(key);
	}

	public void invalidate() {
		this.invalidate = true;
	}

	public boolean isNew() {
		return isNew;
	}

	// /non interface method
	public boolean isInvalidate() {
		return invalidate;
	}

	private void checkInvalidate() {
		if (invalidate)// 使用不可能
			throw new RuntimeException("Session invalidated.");
	}

	public void setInvalidate(boolean invalidate) {
		this.invalidate = invalidate;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
