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
package sdloader.j2ee.servlet;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Mime定義パース用のハンドラ
 * 
 * @author c9katayama
 */
public class MimeParseHandler extends DefaultHandler {

	private FileSavingServlet target;

	private Stack nameStack = new Stack();

	private String extension;

	private String mimeType;

	public MimeParseHandler(FileSavingServlet servlet) {
		target = servlet;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		nameStack.push(qName);
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		char[] c = new char[length];
		System.arraycopy(ch, start, c, 0, length);
		String value = new String(c);

		String name = (String) nameStack.peek();
		if (name.equals("extension"))
			extension = value;
		else if (name.equals("mime-type"))
			mimeType = value;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String name = (String) nameStack.pop();
		if (name.equals("mime-mapping")) {
			if (extension != null && mimeType != null)
				target.addMimeType(extension, mimeType);
			extension = mimeType = null;
		}
	}
}
