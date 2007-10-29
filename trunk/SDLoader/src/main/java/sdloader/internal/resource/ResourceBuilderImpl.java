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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author shot
 */
public class ResourceBuilderImpl implements ResourceBuilder {

	public ResourceBuilderImpl() {
	}

	@SuppressWarnings("deprecation")
	public Map<URL, Resource> build(String filepath) throws IOException {
		final File file = new File(filepath);
		final URL rootUrl = file.toURL();
		final JarFile warfile = new JarFile(file);
		Map<URL, Resource> map = new HashMap<URL, Resource>();
		for (Enumeration<?> en = warfile.entries(); en.hasMoreElements();) {
			JarEntry entry = (JarEntry) en.nextElement();
			final String path = entry.getName();
			InputStream is = null;
			byte[] bytes = null;
			try {
				is = warfile.getInputStream(entry);
				bytes = getBytes(is);
			} catch (IOException ignored) {
				bytes = new byte[0];
			} finally {
				if (is != null) {
					is.close();
				}
			}
			final Resource resourceType = getResourceType(rootUrl, path, bytes);
			map.put(resourceType.getURL(), resourceType);
		}
		return map;
	}

	protected static final byte[] getBytes(InputStream is)
			throws RuntimeException {
		byte[] bytes = null;
		byte[] buf = new byte[8192];
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int n = 0;
			while ((n = is.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, n);
			}
			bytes = baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}

	protected Resource getResourceType(final URL rootUrl, final String path, final byte[] bytes) {
		if (path.endsWith(".class")) {
			return new ClassTypeResourceImpl(rootUrl, path, bytes);
		} else if (path.endsWith(".jar")) {
			return new JarArchiveTypeResourceImpl(rootUrl, path, bytes);
		} else if(path.endsWith("/")) {
			//TODO add branch type as DirectoryTypeResource
		}
		return new FileTypeResourceImpl(rootUrl, path, bytes);
	}

	public static final String replaceFileSeparator(String filepath) {
		return filepath.replace('\\', '/');
	}

}
