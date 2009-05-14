package sdloader.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import sdloader.util.databuffer.TempFileDataBuffer;

public class TempFileDataBufferTest extends TestCase {

	public void test() throws Exception {

		TempFileDataBuffer buffer = new TempFileDataBuffer();
		buffer.write(new byte[] { 1, 2, 3 }, 0, 3);
		buffer.write(new byte[] { 4, 5, 6 }, 1, 2);

		for (int i = 0; i < 10; i++) {
			InputStream is = buffer.getInputStream();
			assertEquals(1, is.read());
			assertEquals(2, is.read());
			assertEquals(3, is.read());
			assertEquals(5, is.read());
			assertEquals(6, is.read());
		}
		assertEquals(5, buffer.getSize());
		buffer.dispose();
	}

	public void test2() throws Exception {

		TempFileDataBuffer buffer = new TempFileDataBuffer();

		buffer.write(new byte[] { 9, 8 }, 0, 2);
		buffer.write(new byte[] { 1, 2, 3 }, 0, 3);
		buffer.write(new byte[] { 4, 5, 6 }, 1, 2);

		for (int i = 0; i < 10; i++) {
			InputStream is = buffer.getInputStream();
			assertEquals(9, is.read());
			assertEquals(8, is.read());
			assertEquals(1, is.read());
			assertEquals(2, is.read());
			assertEquals(3, is.read());
			assertEquals(5, is.read());
			assertEquals(6, is.read());
			assertEquals(-1, is.read());
		}
		assertEquals(7, buffer.getSize());
		buffer.dispose();
	}

	public void test3() throws Exception {
		BufferedImage img = getImage();

		TempFileDataBuffer buf = new TempFileDataBuffer();
		ZipOutputStream zipout = new ZipOutputStream(buf.getOutputStream());
		for (int i = 0; i < 100; i++) {
			ZipEntry entry = new ZipEntry("hoge" + i + ".bmp");
			zipout.putNextEntry(entry);
			ImageIO.write(img, "bmp", zipout);
		}
		zipout.flush();
		zipout.close();

		File tempFile = File.createTempFile("test", ".zip");
		FileOutputStream fout = new FileOutputStream(tempFile);
		ResourceUtil.copyStream(buf.getInputStream(), fout);
		fout.flush();
		fout.close();
		assertEquals(1409202, tempFile.length());
		tempFile.delete();
		buf.dispose();
	}
	private BufferedImage getImage() {
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
		return img;
	}
}
