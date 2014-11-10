package edu.iu.informatics.omics.image;

import java.awt.Color;

public class GalADrawer extends AcidicSugarSegmentDrawer {
	public GalADrawer(int width) {
		super(new Color(250, 0, 0), width,
				AcidicSugarSegmentDrawer.SegmentType.LEFT);
	}
}
