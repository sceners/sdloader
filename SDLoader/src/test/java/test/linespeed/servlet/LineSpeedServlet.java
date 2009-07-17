package test.linespeed.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LineSpeedServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		OutputStream os = resp.getOutputStream();
		byte[] buf = new byte[40000];// 40k
		Arrays.fill(buf, (byte) 1);
		os.write(buf);
		os.flush();
	}
}
