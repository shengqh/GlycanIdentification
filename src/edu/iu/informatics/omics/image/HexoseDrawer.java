package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class HexoseDrawer extends AbstractOligosaccharideDrawer {
	public HexoseDrawer(Color color, int width) {
		super(color, width);
	}

	@Override
	public void draw(Graphics2D g2, int x, int y) {
		int percent90width = (int) (width * 0.9);
		int percent5width = (int) (width * 0.05);

		doDraw(g2, x + percent5width, y + percent5width, percent90width);
	}

	private void doDraw(Graphics2D g2, int x, int y, int realWidth) {
		int percent90width = (int) (realWidth * 0.9);
		int percent5width = (int) (realWidth * 0.05);

		Ellipse2D current = new Ellipse2D.Double(x + percent5width, y
				+ percent5width, percent90width, percent90width);
		g2.setColor(color);
		g2.fill(current);
		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.draw(current);
	}
}
