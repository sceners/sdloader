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

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String commandPath = PathUtil.removeStartSlashIfNeed(req.getPathInfo());
		Command command = CommandFactory.getCommand(commandPath);
		if (command != null) {
			if (command == Command.STOP) {
				SDLoader loader = ProcessScopeContext.getContext()
						.getSDLoader();
				loader.stop();
				res.getWriter().print("SDLoader stop.");
				return;
			}
		}
		res.getWriter().print("Invalide command.");
	}
}
