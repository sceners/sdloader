package sdloader.javaee.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sdloader.SDLoader;
import sdloader.http.ProcessScopeContext;
import sdloader.util.PathUtil;

@SuppressWarnings("serial")
public class CommandServlet extends HttpServlet {

	public static final String COMMAND_STOP = "stop";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String command = PathUtil.removeStartSlashIfNeed(req.getPathInfo());
		if (command != null) {
			if (command.equals(COMMAND_STOP)) {
				SDLoader loader = ProcessScopeContext.getContext()
						.getSDLoader();
				loader.stop();
				return;
			}
		}
		res.getWriter().print("Invalide command.");
	}
}
