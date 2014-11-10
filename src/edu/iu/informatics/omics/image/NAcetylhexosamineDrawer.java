package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class NAcetylhexosamineDrawer extends AbstractOligosaccharideDrawer {
	public NAcetylhexosamineDrawer(Color color, int width) {
		super(color, width);
	}

	@Override
	public void draw(Graphics2D g2, int x, int y) {
		int percent80width = (int) (width * 0.8);
		int percent10witdh = (int) (width * 0.1);
		doDraw(g2, x + percent10witdh, y + percent10witdh, percent80width);
	}

	private void doDraw(Graphics2D g2, int x, int y, int realWidth) {
		Rectangle2D current = new Rectangle2D.Double(x, y, realWidth, realWidth);
		g2.setColor(color);
		g2.fill(current);
		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.draw(current);
	}
}
