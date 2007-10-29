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

package sdloader.internal.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author shot
 */
public class JarArchiveTypeResourceImpl implements ArchiveTypeResource {

	public static final String WEBINF = "WEB-INF/";

	public static final String PROTOCOL = "innerjar:";

	protected String originalPath = null;

	protected String path = null;

	protected byte[] bytes = null;

	protected boolean runtimeNeeded;

	protected URL url;

	public JarArchiveTypeResourceImpl(final URL rootUrl, final String path, final byte[] bytes) {
		this.originalPath = path;
		this.path = path;
		if (path.startsWith(WEBINF)) {
			this.runtimeNeeded = true;
		} else {
			this.runtimeNeeded = false;
		}
		this.bytes = bytes;
		this.url = WarProtocolBuilder.innerjarUrl(rootUrl, originalPath);
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public String getPath() {
		return path;
	}

	public byte[] getResourceAsBytes() {
		return bytes;
	}

	public InputStream getResourceAsInputStream() {
		return new ByteArrayInputStream(bytes);
	}
	
	public boolean isRuntimeNeeded() {
		return runtimeNeeded;
	}
	
	public URL getURL() {
		return url;
	}

}
