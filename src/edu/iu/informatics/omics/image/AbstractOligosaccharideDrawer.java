package edu.iu.informatics.omics.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class AbstractOligosaccharideDrawer implements
		IOligosaccharideDrawer {
	protected int width;

	protected Color color;

	protected final static BasicStroke stroke = new BasicStroke(2.0f);

	public AbstractOligosaccharideDrawer(Color color, int width) {
		this.color = color;
		this.width = width;
	}

	public abstract void draw(Graphics2D g2, int x, int y);
}
