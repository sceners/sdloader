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
package sdloader.j2ee.webxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sdloader.util.CollectionsUtil;

/**
 * filterタグ
 * 
 * @author c9katayama
 */
public class FilterTag {

	private String filterName;

	private String filterClass;

	private List<InitParamTag> initParam = CollectionsUtil.newArrayList();

	public FilterTag() {
		super();
	}

	public String getFilterClass() {
		return filterClass;
	}

	public void setFilterClass(String filterClass) {
		this.filterClass = filterClass;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
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
