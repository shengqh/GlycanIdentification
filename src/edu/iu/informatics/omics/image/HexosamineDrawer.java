package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class HexosamineDrawer extends AbstractOligosaccharideDrawer {
	public HexosamineDrawer(Color color, int width) {
		super(color, width);
	}

	@Override
	public void draw(Graphics2D g2, int x, int y) {
		int percent80width = (int) (width * 0.8);
		int percent10width = (int) (width * 0.1);

		doDraw(g2, x + percent10width, y + percent10width, percent80width);
	}

	private void doDraw(Graphics2D g2, int x, int y, int realWidth) {
		Polygon current = new Polygon();
		current.addPoint(x, y);
		current.addPoint(x + realWidth, y);
		current.addPoint(x + realWidth, y + realWidth);
		g2.setColor(color);
		g2.fill(current);
		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.drawLine(x, y, x + realWidth, y + realWidth);
		Rectangle2D rec = new Rectangle2D.Double(x, y, realWidth, realWidth);
		g2.draw(rec);
	}
}
