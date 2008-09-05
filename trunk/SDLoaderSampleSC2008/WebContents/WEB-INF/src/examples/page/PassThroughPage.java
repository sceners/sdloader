package examples.page;

import java.io.IOException;

import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.PassThrough;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

@RequestScope
@Page("pass")
public class PassThroughPage {

	@Default
	public Navigation _(WebContext context) {
		try {
			context.getResponse().writeAndFlush("done by passthrough.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return PassThrough.pass();
	}
}
