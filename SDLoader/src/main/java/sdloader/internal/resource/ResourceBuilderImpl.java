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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import sdloader.util.CollectionsUtil;

/**
 * @author shot
 */
public class ResourceBuilderImpl implements ResourceBuilder {

	public ResourceBuilderImpl() {
	}

	@SuppressWarnings("deprecation")
	public Map<URL, Resource> build(String filepath) throws IOException {
		final File file = new File(filepath);
		final URL rootUrl = new URL("war:"+file.toURL().toExternalForm());
		final JarInputStream ji = new JarInputStream(new FileInputStream(file));
		return build(rootUrl,ji);
	}
	@SuppressWarnings("deprecation")
	public Map<URL, Resource> build(URL rootUrl,JarInputStream ji) throws IOException {

		Map<URL, Resource> map = CollectionsUtil.newHashMap();
		Map<String,Resource> pathMap = CollectionsUtil.newHashMap();
		final DirectoryTypeResource rootDirResource = new DirectoryTypeResourceImpl(rootUrl,"/");
		map.put(rootDirResource.getURL(),rootDirResource);
		pathMap.put("/",rootDirResource);
		for(;;){
			JarEntry entry = (JarEntry)ji.getNextJarEntry();
			if(entry ==null)
				break;
			final String path = entry.getName();
			byte[] bytes = getBytes(ji);
			final Resource resourceType = getResourceType(rootUrl, path, bytes);
			map.put(resourceType.getURL(), resourceType);
			pathMap.put(path,resourceType);
			
			BranchTypeResource parent = findParentBranchTypeResource(pathMap,path);
			if(parent != null)
				parent.addResource(resourceType);
		}
		return map;
	}
	protected static final BranchTypeResource findParentBranchTypeResource(Map<String,Resource> pathMap,String path){
		if(path.endsWith("/"))
			path = path.substring(0,path.length()-1);
		int sepIndex = path.lastIndexOf("/");
		String parentPath;
		if(sepIndex < 0){
			parentPath = "/";
		}else{
			parentPath = path.substring(0,sepIndex+1);
		}
		return (BranchTypeResource)pathMap.get(parentPath);		
	}
	protected static final byte[] getBytes(InputStream is){
		byte[] bytes = null;
		byte[] buf = new byte[8192];
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int n = 0;
			while ((n = is.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, n);
			}
			bytes = baos.toByteArray();
		} catch (IOException ignore) {
			 bytes = new byte[0];
		}
		return bytes;
	}

	protected Resource getResourceType(final URL rootUrl, final String path, final byte[] bytes) throws IOException {
		if (path.endsWith(".class")) {
			return new ClassTypeResourceImpl(rootUrl, path, bytes);
		} else if (path.endsWith(".jar")) {
				JarArchiveTypeResourceImpl jarResource = 
					new JarArchiveTypeResourceImpl(rootUrl, path, bytes);
				if(jarResource.isRuntimeNeeded()){
					//resolve jar resources
					Map<URL, Resource> jarResources = 
						build(jarResource.getURL(),
								new JarInputStream(new ByteArrayInputStream(bytes)));
					jarResource.setArchiveResources(jarResources);
				}
				return jarResource;
		} else if(path.endsWith("/")) {
			return new DirectoryTypeResourceImpl(rootUrl,path);
		}
		return new FileTypeResourceImpl(rootUrl, path, bytes);
	}

	public static final String replaceFileSeparator(String filepath) {
		return filepath.replace('\\', '/');
	}

	public static final String stripJarArchivePath(String filepath) {
		return filepath.substring(filepath.indexOf("!")+1,filepath.length());
	}
}
