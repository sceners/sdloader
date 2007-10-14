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
package sdloader.http;

import java.util.List;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.CollectionsUtil;

/**
 * HttpRequestProcessorPool
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpRequestProcessorPool {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(HttpRequestProcessor.class);

	private int maxThreadPoolNum;

	//TODO change list to concurrent queue.
	private List<HttpRequestProcessor> processorPool = CollectionsUtil.newLinkedList();

	public HttpRequestProcessorPool(int maxThreadPoolNum) {
		this.maxThreadPoolNum = maxThreadPoolNum;
		for (int i = 0; i < maxThreadPoolNum; ++i) {
			HttpRequestProcessor processor = new HttpRequestProcessor(
					"HttpRequestProcessor:init" + i);
			processor.start();
			processorPool.add(processor);
		}
	}

	public HttpRequestProcessor borrowProcessor() {
		synchronized (processorPool) {
			if (processorPool.isEmpty()) {
				HttpRequestProcessor processor = new HttpRequestProcessor(
						"HttpRequestProcessor:" + System.currentTimeMillis());
				processor.start();
				log.debug("create " + processor.getName());
				return processor;
			} else {
				HttpRequestProcessor processor = (HttpRequestProcessor) processorPool
						.remove(0);
				log.debug("reuse " + processor.getName());
				return processor;
			}
		}
	}

	public void returnProcessor(HttpRequestProcessor processor) {
		synchronized (processorPool) {
			if (processorPool.size() < maxThreadPoolNum) {
				log.debug("return " + processor.getName());
				processorPool.add(processor);
			} else {
				log.debug("stop " + processor.getName());
				processor.stopProcessor();
			}
		}
	}
}
