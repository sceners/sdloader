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

package sdloader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author shot
 */
public class Main {

	public static void main(String[] args) throws Exception {
		ResourceBuilder builder = new ResourceBuilderImpl();
		final Map<String, Resource> map = builder.build("test.war");
		BytesBasedClassLoader loader = new BytesBasedClassLoader(Thread
				.currentThread().getContextClassLoader(), map);
		final Class loadClass = loader.loadClass("it00001.SelectOneRadioDto",
				true);
		System.out.println(loadClass);
		System.out.println(loadClass.newInstance());
		
		final Resource resource = map.get("selectOneRadio.html");
		byte[] bytes = resource.getResourceAsBytes();
		ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
		InputStreamReader reader = new InputStreamReader(bai);
		BufferedReader br = new BufferedReader(reader);
		String s = null;
		while((s = br.readLine()) != null) {
			System.out.println(s);
		}
	}
}
