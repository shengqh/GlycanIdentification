package edu.iu.informatics.omics.image;

import java.awt.Color;

public class IdoADrawer extends AcidicSugarSegmentDrawer {
	public IdoADrawer(int width) {
		super(new Color(150,100,50), width, AcidicSugarSegmentDrawer.SegmentType.BOTTOM);
	}
}
