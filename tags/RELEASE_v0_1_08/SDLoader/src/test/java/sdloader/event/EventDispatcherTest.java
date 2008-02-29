package sdloader.event;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class EventDispatcherTest extends TestCase{

	static class EventTest extends Event{
		public EventTest(String type,Object source) {
			super(type,source);
		}
	}
	static interface ListenerTest{
		public void handle(EventTest e);
	}
	
	public void testDispatch(){
		EventDispatcher<ListenerTest,EventTest> dispatcher = new EventDispatcher<ListenerTest,EventTest>("handle");
		final List<String> handleString = new ArrayList<String>();
		
		dispatcher.addEventListener("handle",new ListenerTest(){
			public void handle(EventTest e) {
				handleString.add((String)e.getSource());
			}
		});
		
		dispatcher.dispatchEvent(new EventTest("handle","ok"));
		
		assertEquals(1,handleString.size());
		assertEquals("ok",handleString.get(0));		
	}
	public void testDetectMethod(){
		EventDispatcher<ListenerTest,EventTest> dispatcher 
			= new EventDispatcher<ListenerTest,EventTest>(ListenerTest.class);
		final List<String> handleString = new ArrayList<String>();
		
		dispatcher.addEventListener("handle",new ListenerTest(){
			public void handle(EventTest e) {
				handleString.add((String)e.getSource());
			}
		});		
		dispatcher.dispatchEvent(new EventTest("handle","ok"));
		
		assertEquals(1,handleString.size());
		assertEquals("ok",handleString.get(0));		
	}
	public void testRemove(){
		EventDispatcher<ListenerTest,EventTest> dispatcher = new EventDispatcher<ListenerTest,EventTest>("handle");
		final List<String> handleString = new ArrayList<String>();
		
		ListenerTest l1 = new ListenerTest(){
			public void handle(EventTest e) {
				handleString.add((String)e.getSource()+"l1");
			}
		};
		ListenerTest l2 = new ListenerTest(){
			public void handle(EventTest e) {
				handleString.add((String)e.getSource()+"l2");
			}
		};
		
		dispatcher.addEventListener("handle",l1);
		dispatcher.addEventListener("handle",l2);
		dispatcher.addEventListener("handle",l1);		
		dispatcher.addEventListener("handle",l2);
		
		dispatcher.removeEventListener("handle",l1);
		
		dispatcher.dispatchEvent(new EventTest("handle","ok"));
		
		assertEquals(2,handleString.size());
		assertEquals("okl2",handleString.get(0));		
		assertEquals("okl2",handleString.get(1));
	}

}
