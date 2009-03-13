package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SWTServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String type = req.getParameter("type");
		if("color".equals(type)){
			color(req,res);
			return ;
		}
		if("messageBox".equals(type)){
			messageBox(req,res);
			return ;
		}		
	}
	protected void color(HttpServletRequest req,HttpServletResponse res)throws IOException{
		Shell shell = new Shell(SWT.ON_TOP);
		ColorDialog dialog = new ColorDialog(shell);
		RGB color = dialog.open();
		PrintWriter w = res.getWriter();
		w.print(color.toString());
		w.flush();
		
	}
	protected void messageBox(HttpServletRequest req,HttpServletResponse res) throws IOException{
		Shell shell = new Shell(SWT.ON_TOP);
		try{
			MessageBox msg = new MessageBox(shell, SWT.YES | SWT.NO );
			msg.setMessage("YES‚©NO‚ð‘I‘ð‚µ‚Ä‚­‚¾‚³‚¢");
			int result = msg.open();
	
			PrintWriter w = res.getWriter();
			if (result == SWT.YES) {
				w.println("YES!!");
			} else if (result == SWT.NO) {
				w.println("NO!!");
			}
			w.flush();
		}finally{
			shell.close();
		}

	}
}
