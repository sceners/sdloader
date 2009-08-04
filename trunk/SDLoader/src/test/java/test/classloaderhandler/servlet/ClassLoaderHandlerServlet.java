package test.classloaderhandler.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClassLoaderHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Class.forName("test.classloaderhandler.servlet.ClassA");
			throw new RuntimeException("NG");
		} catch (ClassNotFoundException e) {

		}
		try {
			Class.forName("test.classloaderhandler.servlet.ClassB");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		InputStream is = getClass().getResourceAsStream("resourceA.txt");
		resp.getWriter().write(
				new BufferedReader(new InputStreamReader(is)).readLine());
	}
}
