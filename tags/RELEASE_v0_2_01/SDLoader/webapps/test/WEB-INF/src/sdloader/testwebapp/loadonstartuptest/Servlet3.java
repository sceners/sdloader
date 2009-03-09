package sdloader.testwebapp.loadonstartuptest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import sdloader.StartUpList;


@SuppressWarnings("serial")
public class Servlet3 extends HttpServlet {

	@Override
	public void init() throws ServletException {
		System.out.println("Servlet3 init.");
		StartUpList.startUpList.add("Servlet3");
	}
}
