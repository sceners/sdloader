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
 * HttpProcessorPool
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpProcessorPool {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(HttpProcessor.class);

	private int nextProcessorNo;

	private int maxThreadPoolNum;

	private List<HttpProcessor> processorPool = CollectionsUtil.newLinkedList();
	
	private boolean stop;
	
	public HttpProcessorPool(int maxThreadPoolNum) {
		this.maxThreadPoolNum = maxThreadPoolNum;
		for (int i = 0; i < maxThreadPoolNum; ++i) {
			String processorName = "HttpProcessor:init" + i;
			HttpProcessor processor = createProcessor(processorName);
			processorPool.add(processor);
		}
		nextProcessorNo = maxThreadPoolNum + 1;
	}

	private HttpProcessor createProcessor(String processorName) {
		HttpProcessor processor = new HttpProcessor(processorName);
		processor.start();
		log.debug("create " + processor.getName());		
		return processor;
	}

	public synchronized HttpProcessor borrowProcessor() {
		if (processorPool.isEmpty()) {
			String processorName = "HttpProcessor:" + (nextProcessorNo++);
			return createProcessor(processorName);
		} else {
			HttpProcessor processor = processorPool.remove(0);
			log.debug("reuse " + processor.getName());
			return processor;
		}
	}

	public synchronized void returnProcessor(HttpProcessor processor) {
		if(stop){
			return;
		}
		if (processorPool.size() < maxThreadPoolNum) {
			log.debug("return " + processor.getName());
			processorPool.add(processor);
		} else {
			log.debug("stop " + processor.getName());
			processor.stopProcessor();
		}
	}
	public synchronized void stop(){
		if(!stop){
			stop = true;
			for(HttpProcessor processor:processorPool){
				processor.stopProcessor();
			}
		}
	}
}
