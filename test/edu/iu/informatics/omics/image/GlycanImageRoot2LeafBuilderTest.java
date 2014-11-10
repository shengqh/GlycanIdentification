package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;

public class GlycanImageRoot2LeafBuilderTest extends TestCase {
	public void testDraw() throws Exception {
		GlycanImageRoot2LeafBuilder builder = new GlycanImageRoot2LeafBuilder();
		
		Glycan glycan = new Glycan("Test",
				"Man(3-1Man(2-1Man(2-1Man,3-1Fuc),6-1ManNAc3-1Fuc),4-1Fuc,6-1ManNAc3-1Man2-1Man)");
		OligosaccharideImageLevelBuilder.assignDrawLevel(glycan);

		Dimension dim = builder.getDimension(glycan, 40);
		BufferedImage image = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(0, 0, dim.width, dim.height));

		builder.drawStructure(glycan, g2, 40, dim);

		FileOutputStream fos = new FileOutputStream("c:\\temp_r2l.png");
		ImageIO.write(image, "png", fos);
	}
}
