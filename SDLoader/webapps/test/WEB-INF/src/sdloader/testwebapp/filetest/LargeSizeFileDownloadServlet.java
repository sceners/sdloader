package sdloader.testwebapp.filetest;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LargeSizeFileDownloadServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		BufferedImage img = new BufferedImage(800, 600,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setPaint(new GradientPaint(0, 0, Color.YELLOW, img.getWidth(), img
				.getHeight(), Color.BLUE, true));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		for (int i = 0; i < img.getHeight(); i += 10) {
			g.setColor(new Color(i + 1 * 100 + 1 * 10000));
			g.drawLine(0, i, img.getWidth(), i);
		}
		g.dispose();

		ZipOutputStream zipout = new ZipOutputStream(resp.getOutputStream());
		ZipOutputStream zipout2 = new ZipOutputStream(new FileOutputStream(new File("c:/t.zip")));
		for (int i = 0; i < 200; i++) {
			ZipEntry entry = new ZipEntry("hoge" + i + ".bmp");
			zipout.putNextEntry(entry);
			ImageIO.write(img, "bmp", zipout);
			ZipEntry entry2 = new ZipEntry("hoge" + i + ".bmp");
			zipout2.putNextEntry(entry2);
			ImageIO.write(img, "bmp", zipout2);			
		}
		zipout.close();
		zipout2.close();
	}
}
