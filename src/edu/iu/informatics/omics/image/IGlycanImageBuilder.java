package edu.iu.informatics.omics.image;

import java.awt.Dimension;
import java.awt.Graphics2D;

import edu.iu.informatics.omics.Glycan;

public interface IGlycanImageBuilder {
	Dimension getDimension(Glycan glycan, int oligosaccharideWidth);

	void drawStructure(Glycan glycan, Graphics2D g2, int oligosaccharideWidth,
			Dimension dim);

}
