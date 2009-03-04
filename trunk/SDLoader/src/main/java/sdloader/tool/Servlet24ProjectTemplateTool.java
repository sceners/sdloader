/*
 * Copyright 2005-2008 the original author or authors.
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
package sdloader.tool;

import java.io.InputStream;

import sdloader.tool.helper.ProjectTemplateHelper;

public class Servlet24ProjectTemplateTool {

	public static void main(String[] args) {
		System.out.println("Servlet API 2.4 project template create.");
		InputStream webXml = Servlet24ProjectTemplateTool.class
				.getResourceAsStream("/sdloader/resource/template/webxml24.xml");
		InputStream indexHtml = Servlet24ProjectTemplateTool.class
				.getResourceAsStream("/sdloader/resource/template/index.html");
		InputStream mainTemplate = Servlet24ProjectTemplateTool.class
				.getResourceAsStream("/sdloader/resource/template/SDLoaderStartMain.template");
		String webContent = "WebContent";
		ProjectTemplateHelper.execute(webContent, webXml, indexHtml,
				mainTemplate);
		System.exit(0);
	}
}
