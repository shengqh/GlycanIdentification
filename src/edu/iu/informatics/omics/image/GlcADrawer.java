package edu.iu.informatics.omics.image;

import java.awt.Color;

public class GlcADrawer extends AcidicSugarSegmentDrawer {
	public GlcADrawer(int width) {
		super(new Color(0, 0, 250), width, AcidicSugarSegmentDrawer.SegmentType.TOP);
	}
}
