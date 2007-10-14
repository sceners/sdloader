/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package lucy;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import lucy.annotation.Component;
import lucy.util.AssertionUtil;

/**
 * @author shot
 */
public class RegistryImpl implements Registry {

	private static final ConcurrentHashMap<Class<? extends Annotation>, Behavior> behaviors = new ConcurrentHashMap<Class<? extends Annotation>, Behavior>();

	private AnnotationRegistry annotationRegistry = new AnnotationRegistry() {

		@SuppressWarnings("unchecked")
		public void registerBehavior(Class<? extends Annotation> clazz,
				Behavior behavior) {
			AssertionUtil.assertNotNull(clazz);
			AssertionUtil.assertNotNull(behavior);
			behaviors.put(clazz, behavior);
		}

		public Behavior getBehavior(Class<? extends Annotation> clazz) {
			AssertionUtil.assertNotNull(clazz);
			return behaviors.get(clazz);
		}
	};

	public void destroy() {
	}

	public <T> T get(Class<? extends T> key) {
		return null;
	}

	public void init() {
		annotationRegistry.registerBehavior(Component.class, new Behavior() {

			public <T> T execute(Class<? extends T> clazz, T t, PropertyDesc propertyDesc) {
				final Object object = get(propertyDesc.getPropertyType());
				propertyDesc.setValue(t, object);
				return t;
			}
			
		});
	}

	public <T> void register(Class<T> componentClass) {
	}

}
