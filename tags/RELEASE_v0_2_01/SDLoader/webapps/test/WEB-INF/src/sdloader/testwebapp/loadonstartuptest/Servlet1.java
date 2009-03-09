package sdloader.testwebapp.loadonstartuptest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import sdloader.StartUpList;


@SuppressWarnings("serial")
public class Servlet1 extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("Servlet1 init.");
		StartUpList.startUpList.add("Servlet1");
	}

}
