package examples.handler;

import org.t2framework.contexts.WebContext;
import org.t2framework.handler.ExceptionHandler;
import org.t2framework.navigation.Forward;
import org.t2framework.spi.Navigation;

import commons.exception.ResourceNotFoundRuntimeException;

public class ResourceNotFoundRuntimeExceptionHandlerImpl implements
		ExceptionHandler<ResourceNotFoundRuntimeException, Exception> {

	@Override
	public Navigation handleException(ResourceNotFoundRuntimeException t,
			WebContext context) throws Exception {
		System.out.println(t.getMessage());
		context.getRequest().setAttribute("errormessage", t.getMessage());
		return Forward.to("jsp/not_found.jsp");
	}

	@Override
	public boolean isTargetException(Throwable t) {
		return (t != null) ? t.getClass() == ResourceNotFoundRuntimeException.class
				: false;
	}

}
