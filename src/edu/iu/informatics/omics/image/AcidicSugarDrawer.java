package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class AcidicSugarDrawer extends AbstractOligosaccharideDrawer {
	public AcidicSugarDrawer(Color color, int width) {
		super(color, width);
	}

	@Override
	public void draw(Graphics2D g2, int x, int y) {
		int radious = width / 2;
		
		Polygon current = PolygonUtils.pointsToPolygon(PolygonUtils.getDiamond(x
				+ radious, y + radious, radious));
		g2.setColor(color);
		g2.fill(current);
		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.draw(current);
	}
}
