package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

public class DrawerTest extends TestCase {
	public void testDraw() throws Exception {
		int width = 400;
		int height = 1200;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(0, 0, width, height));

		new HexoseDrawer(Color.YELLOW, 30).draw(g2, 10, 10);
		new HexoseDrawer(Color.BLUE, 40).draw(g2, 100, 10);
		new HexoseDrawer(Color.GREEN, 50).draw(g2, 200, 10);

		new NAcetylhexosamineDrawer(Color.YELLOW, 30).draw(g2, 10, 100);
		new NAcetylhexosamineDrawer(Color.BLUE, 40).draw(g2, 100, 100);
		new NAcetylhexosamineDrawer(Color.GREEN, 50).draw(g2, 200, 100);

		new HexosamineDrawer(Color.YELLOW, 30).draw(g2, 10, 200);
		new HexosamineDrawer(Color.BLUE, 40).draw(g2, 100, 200);
		new HexosamineDrawer(Color.GREEN, 50).draw(g2, 200, 200);

		new FucoseDrawer(30).draw(g2, 10, 300);
		new FucoseDrawer(40).draw(g2, 100, 300);
		new FucoseDrawer(50).draw(g2, 200, 300);

		new XyloseDrawer(30).draw(g2, 10, 400);
		new XyloseDrawer(40).draw(g2, 100, 400);
		new XyloseDrawer(50).draw(g2, 200, 400);

		new Neu5AcDrawer(30).draw(g2, 10, 500);
		new Neu5AcDrawer(40).draw(g2, 100, 500);
		new Neu5AcDrawer(50).draw(g2, 200, 500);

		new Neu5GcDrawer(30).draw(g2, 10, 600);
		new Neu5GcDrawer(40).draw(g2, 100, 600);
		new Neu5GcDrawer(50).draw(g2, 200, 600);

		new KDNDrawer(30).draw(g2, 10, 700);
		new KDNDrawer(40).draw(g2, 100, 700);
		new KDNDrawer(50).draw(g2, 200, 700);

		new GlcADrawer(30).draw(g2, 10, 800);
		new GlcADrawer(40).draw(g2, 100, 800);
		new GlcADrawer(50).draw(g2, 200, 800);

		new IdoADrawer(30).draw(g2, 10, 900);
		new IdoADrawer(40).draw(g2, 100, 900);
		new IdoADrawer(50).draw(g2, 200, 900);

		new GalADrawer(30).draw(g2, 10, 1000);
		new GalADrawer(40).draw(g2, 100, 1000);
		new GalADrawer(50).draw(g2, 200, 1000);

		new ManADrawer(30).draw(g2, 10, 1100);
		new ManADrawer(40).draw(g2, 100, 1100);
		new ManADrawer(50).draw(g2, 200, 1100);

		FileOutputStream fos = new FileOutputStream("c:\\temp.png");
		ImageIO.write(image, "png", fos);
	}

}
