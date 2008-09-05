package examples.page;

import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.spi.Navigation;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import commons.annotation.composite.RequestScope;

@RequestScope
@Page("twitter/publicTimeline")
public class TwitterPage {

	@Default
	public Navigation getPublicTimeLine(final WebContext context) {
		Twitter twitter = new Twitter();
		try {
			context.getRequest().setAttribute("timeline",
					twitter.getPublicTimeline());
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
		return Forward.to("/jsp/timeLine.jsp");
	}

}
