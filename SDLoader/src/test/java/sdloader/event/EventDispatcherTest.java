/*
 * Copyright 2005-2010 the original author or authors.
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
package sdloader.event;

import java.util.ArrayList;
import java.util.List;

import sdloader.util.event.Event;
import sdloader.util.event.EventDispatcher;

import junit.framework.TestCase;

@SuppressWarnings("unchecked")
public class EventDispatcherTest extends TestCase {

	static class EventT extends Event {
		public EventT(String type, Object source) {
			super(type, source);
		}
	}

	static interface ListenerT {
		public void handle(EventT e);
	}

	public void testDispatch() {
		EventDispatcher dispatcher = new EventDispatcher<ListenerT, EventT>(
				"handle");
		final List<String> handleString = new ArrayList<String>();

		dispatcher.addEventListener("handle", new ListenerT() {
			public void handle(EventT e) {
				handleString.add((String) e.getSource());
			}
		});

		dispatcher.dispatchEvent(new EventT("handle", "ok"));

		assertEquals(1, handleString.size());
		assertEquals("ok", handleString.get(0));
	}

	public void testDetectMethod() {
		EventDispatcher<ListenerT, EventT> dispatcher = new EventDispatcher<ListenerT, EventT>(
				ListenerT.class);
		final List<String> handleString = new ArrayList<String>();

		dispatcher.addEventListener("handle", new ListenerT() {
			public void handle(EventT e) {
				handleString.add((String) e.getSource());
			}
		});
		dispatcher.dispatchEvent(new EventT("handle", "ok"));

		assertEquals(1, handleString.size());
		assertEquals("ok", handleString.get(0));
	}

	public void testRemove() {
		EventDispatcher<ListenerT, EventT> dispatcher = new EventDispatcher<ListenerT, EventT>(
				"handle");
		final List<String> handleString = new ArrayList<String>();

		ListenerT l1 = new ListenerT() {
			public void handle(EventT e) {
				handleString.add((String) e.getSource() + "l1");
			}
		};
		ListenerT l2 = new ListenerT() {
			public void handle(EventT e) {
				handleString.add((String) e.getSource() + "l2");
			}
		};

		dispatcher.addEventListener("handle", l1);
		dispatcher.addEventListener("handle", l2);
		dispatcher.addEventListener("handle", l1);
		dispatcher.addEventListener("handle", l2);

		dispatcher.removeEventListener("handle", l1);

		dispatcher.dispatchEvent(new EventT("handle", "ok"));

		assertEquals(2, handleString.size());
		assertEquals("okl2", handleString.get(0));
		assertEquals("okl2", handleString.get(1));
	}

}
