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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * IteratorのEnumerationラップクラス
 * 
 * @author c9katayama
 */
public class IteratorEnumeration implements Enumeration {

	private Iterator itr;

	public IteratorEnumeration() {
	}

	public IteratorEnumeration(Iterator itr) {
		this.itr = itr;
	}

	public boolean hasMoreElements() {
		if (itr == null)
			return false;
		else
			return itr.hasNext();
	}

	public Object nextElement() {
		if (itr == null)
			return null;
		else
			return itr.next();
	}
}
