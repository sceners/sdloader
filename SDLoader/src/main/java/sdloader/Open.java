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

import sdloader.SDLoader;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
/**
 * SDLoaderをオープンします。
 * 
 * @author c9katayama
 */
public class Open {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(Open.class);

	public static void main(String[] args) {
		try {
			SDLoader server = new SDLoader();
			CommandMonitor.monitor(8089, "SDLoader", server);
			server.addEventListener(LifecycleEvent.AFTER_STOP,new LifecycleListener(){
				public void handleLifecycle(LifecycleEvent<?> event) {
					System.exit(0);					
				}
			});
			server.start();
		} catch (Throwable e) {
			log.error("SDLoader catch error.",e);			
		}
	}
}
