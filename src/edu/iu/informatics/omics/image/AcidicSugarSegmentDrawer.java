package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

public class AcidicSugarSegmentDrawer extends AbstractOligosaccharideDrawer {
	public static enum SegmentType {
		TOP, LEFT, BOTTOM, RIGHT
	};

	private SegmentType sType;

	public AcidicSugarSegmentDrawer(Color color, int width, SegmentType sType) {
		super(color, width);
		this.sType = sType;
	}

	@Override
	public void draw(Graphics2D g2, int x, int y) {
		int radious = width / 2;

		List<Point> points = PolygonUtils.getDiamond(x + radious, y + radious,
				radious);

		Polygon current = PolygonUtils.pointsToPolygon(points);
		g2.setColor(Color.WHITE);
		g2.fill(current);

		Point[] segment;
		if (sType == SegmentType.TOP) {
			segment = new Point[] { points.get(0), points.get(1), points.get(3) };
		} else if (sType == SegmentType.LEFT) {
			segment = new Point[] { points.get(0), points.get(1), points.get(2) };
		} else if (sType == SegmentType.BOTTOM) {
			segment = new Point[] { points.get(1), points.get(2), points.get(3) };
		} else {
			segment = new Point[] { points.get(0), points.get(2), points.get(3) };
		}

		Polygon segmentPolygon = PolygonUtils.pointsToPolygon(segment);
		g2.setColor(color);
		g2.fill(segmentPolygon);

		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.draw(segmentPolygon);
		g2.draw(current);
	}
}
