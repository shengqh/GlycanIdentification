package edu.iu.informatics.omics.image;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class PolygonUtils {
	public static List<Point> getTriAngle(int centerX, int centerY, int radious) {
		List<Point> result = new ArrayList<Point>();

		double angle1 = Math.PI / 3;

		double a = radious * Math.cos(angle1);
		double b = radious * Math.sin(angle1);

		result.add(new Point(centerX, centerY - radious));

		result.add(new Point((int) (centerX - b), (int) (centerY + a)));

		result.add(new Point((int) (centerX + b), (int) (centerY + a)));

		return result;
	}

	public static List<Point> getDiamond(int centerX, int centerY, int radious) {
		List<Point> result = new ArrayList<Point>();

		result.add(new Point(centerX, centerY - radious));

		result.add(new Point(centerX - radious, centerY));

		result.add(new Point(centerX, centerY + radious));

		result.add(new Point(centerX + radious, centerY));

		return result;
	}

	public static List<Point> getFiveStar(int centerX, int centerY, int radious) {
		List<Point> result = new ArrayList<Point>();

		double angle1 = 2 * Math.PI / 5;

		double c = radious * Math.sin(angle1) / (1 + Math.cos(angle1));
		double a = c * Math.cos(angle1);
		double b = c * Math.sin(angle1);

		result.add(new Point(centerX, centerY - radious));

		result.add(new Point((int) (centerX - a), (int) (centerY - radious + b)));

		result.add(new Point((int) (centerX - radious * Math.sin(angle1)),
				(int) (centerY - radious * Math.cos(angle1))));

		result.add(new Point((int) (centerX - (c + 2 * a) * Math.cos(angle1)),
				(int) (centerY - radious + (c + 2 * a) * Math.sin(angle1))));

		result.add(new Point((int) (centerX - radious * Math.sin(angle1 / 2)),
				(int) (centerY + radious * Math.cos(angle1 / 2))));

		result.add(new Point((int) (centerX), (int) (centerY + a
				/ Math.sin(angle1 / 2))));

		result.add(new Point((int) (centerX + radious * Math.sin(angle1 / 2)),
				(int) (centerY + radious * Math.cos(angle1 / 2))));

		result.add(new Point((int) (centerX + (c + 2 * a) * Math.cos(angle1)),
				(int) (centerY - radious + (c + 2 * a) * Math.sin(angle1))));

		result.add(new Point((int) (centerX + radious * Math.sin(angle1)),
				(int) (centerY - radious * Math.cos(angle1))));

		result.add(new Point((int) (centerX + a), (int) (centerY - radious + b)));

		return result;
	}

	public static Polygon pointsToPolygon(List<Point> points) {
		return pointsToPolygon(points.toArray(new Point[0]));
	}

	public static Polygon pointsToPolygon(Point[] points) {
		Polygon result = new Polygon();

		for (Point p : points) {
			result.addPoint(p.x, p.y);
		}
		return result;
	}
}
