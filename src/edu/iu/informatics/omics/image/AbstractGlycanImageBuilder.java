package edu.iu.informatics.omics.image;

import java.awt.Dimension;
import java.awt.Graphics2D;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.OligosaccharideUtils;

public abstract class AbstractGlycanImageBuilder implements IGlycanImageBuilder {
	public Dimension getDimension(Glycan glycan, int oligosaccharideWidth) {
		double maxLevel = OligosaccharideImageLevelBuilder.getMaxLevel(glycan);
		int maxDepth = OligosaccharideUtils.getMaxDepth(glycan.getReducingTerm());
		int levelHeight = 2 * oligosaccharideWidth;
		return new Dimension(maxDepth * levelHeight, (int) (maxLevel * levelHeight));
	}

	public abstract void drawStructure(Glycan glycan, Graphics2D g2,
			int oligosaccharideWidth, Dimension dim);
}
