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
package sdloader.javaee.webxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sdloader.util.CollectionsUtil;

/**
 * servletタグ
 * 
 * @author c9katayama
 */
public class ServletTag {

	private String servletName;

	private String displayName;

	private String servletClass;

	private List<InitParamTag> initParam = CollectionsUtil.newArrayList();

	private int loadOnStartup;

	public ServletTag() {
		super();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public String getServletClass() {
		return servletClass;
	}

	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public void addInitParam(InitParamTag initParam) {
		this.initParam.add(initParam);
	}

	public String getInitParam(String paramName) {
		for (Iterator<InitParamTag> itr = initParam.iterator(); itr.hasNext();) {
			InitParamTag initParamTag = itr.next();
			if (initParamTag.getParamName().equals(paramName))
				return initParamTag.getParamValue();
		}
		return null;
	}

	public List<InitParamTag> getInitParamList() {
		return initParam;
	}
}
