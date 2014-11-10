package edu.iu.informatics.omics.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.image.IGlycanImageBuilder;
import edu.iu.informatics.omics.image.OligosaccharideImageLevelBuilder;
import edu.iu.informatics.omics.io.GlycanIOUtils;
import edu.iu.informatics.omics.io.IGlycanParser;

public class GlycanStructureImageBatchBuilder implements IFileProcessor {
	private IGlycanImageBuilder builder;

	private IGlycanParser parser;

	private int oligosacchrideWidth = 20;

	private String glycanStrFile;

	public GlycanStructureImageBatchBuilder(IGlycanImageBuilder builder,
			IGlycanParser parser, int oligosacchrideWidth, String glycanStrFile) {
		super();
		this.builder = builder;
		this.parser = parser;
		this.oligosacchrideWidth = oligosacchrideWidth;
		this.glycanStrFile = glycanStrFile;
	}

	public List<String> process(String saveDirectory) throws Exception {
		List<Glycan> glycans = GlycanIOUtils.readGlycanStructures(parser,
				glycanStrFile);

		for (Glycan glycan : glycans) {
			OligosaccharideImageLevelBuilder.assignDrawLevel(glycan);

			Dimension dim = builder.getDimension(glycan, oligosacchrideWidth);

			BufferedImage image = new BufferedImage((int) dim.getWidth(), (int) dim
					.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = image.createGraphics();
			g2.setColor(Color.WHITE);
			g2.fill(new Rectangle2D.Double(0, 0, dim.getWidth(), dim.getHeight()));

			builder.drawStructure(glycan, g2, oligosacchrideWidth, dim);

			FileOutputStream fos = new FileOutputStream(saveDirectory + "\\"
					+ glycan.getName() + ".png");
			try {
				ImageIO.write(image, "png", fos);
			} finally {
				fos.close();
			}
		}
		return Arrays.asList(new String[] { saveDirectory });
	}

}
