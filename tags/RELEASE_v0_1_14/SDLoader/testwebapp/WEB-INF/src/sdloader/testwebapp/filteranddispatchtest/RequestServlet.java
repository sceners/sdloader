package sdloader.testwebapp.filteranddispatchtest;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("RequestServlet");

		RequestDispatcher dispatcher = req
				.getRequestDispatcher("includeServlet");
		dispatcher.include(req, resp);

		dispatcher = req.getRequestDispatcher("forwardServlet");
		dispatcher.forward(req, resp);
	}
}
