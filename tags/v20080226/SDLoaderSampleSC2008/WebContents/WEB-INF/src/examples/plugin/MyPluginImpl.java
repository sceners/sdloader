package examples.plugin;

import org.t2framework.action.ActionContext;
import org.t2framework.plugin.AbstractPlugin;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.SingletonScope;
import commons.meta.MethodDesc;
import commons.util.Logger;


/**
 * プラグインのサンプル.パフォーマンス測定用.
 * 
 * @author shot
 */
@SingletonScope
public class MyPluginImpl extends AbstractPlugin
{

    private static Logger logger = Logger.getLogger(MyPluginImpl.class);


    @Override
    public Navigation beforeActionInvoke(ActionContext actionContext,
        MethodDesc targetMethod, Object page, Object[] args)
    {
        long start = System.currentTimeMillis();
        logger.debug("[perf] start = " + start);
        actionContext.getRequest().setAttribute("perf_time", start);
        return super.afterActionInvoke(actionContext, targetMethod, page, args);
    }


    @Override
    public Navigation afterActionInvoke(ActionContext actionContext,
        MethodDesc targetMethod, Object page, Object[] args)
    {
        Long attr = actionContext.getRequest().removeAttribute("perf_time");
        long start = attr;
        logger.debug("[perf] perf = " + (System.currentTimeMillis() - start));
        return super
            .beforeActionInvoke(actionContext, targetMethod, page, args);
    }

}
