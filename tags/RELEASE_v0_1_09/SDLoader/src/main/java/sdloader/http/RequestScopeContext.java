package sdloader.http;

import java.util.HashMap;
import java.util.Map;

public class RequestScopeContext {

	private static ThreadLocal<RequestScopeContext> threadLocal = new ThreadLocal<RequestScopeContext>();
	
	private Map<Object,Object> attribute;
	
	private RequestScopeContext(){		
	}
	public static RequestScopeContext getContext(){
		RequestScopeContext context = threadLocal.get();
		if(context==null){
			init();
			context = threadLocal.get();
		}
		return context;
	}
	public static void init(){
		threadLocal.set(new RequestScopeContext());
	}
	public static void destroy(){
		threadLocal.remove();
	}
	
	public void setAttribute(Object key,Object value){
		if(attribute==null){
			attribute = new HashMap<Object, Object>();
		}
		attribute.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(Object key){
		if(attribute==null){
			return null;
		}
		return (T)attribute.get(key);
	}
}
