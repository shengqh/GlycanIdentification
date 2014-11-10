package edu.iu.informatics.omics.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import edu.iu.informatics.omics.io.IGlycanParser;

public class GlycanStructureImageBuilder implements IFileProcessor {
	private IGlycanImageBuilder builder;

	private IGlycanParser parser;

	private int oligosaccharideWidth = 20;

	private String glycanStr;

	public GlycanStructureImageBuilder(IGlycanImageBuilder builder,
			IGlycanParser parser, int oligosaccharideWidth, String glycanStr) {
		super();
		this.builder = builder;
		this.parser = parser;
		this.oligosaccharideWidth = oligosaccharideWidth;
		this.glycanStr = glycanStr;
	}

	public List<String> process(String saveFile) throws Exception {
		Glycan glycan = new Glycan("Test", parser, glycanStr);
		OligosaccharideImageLevelBuilder.assignDrawLevel(glycan);

		Dimension dim = builder.getDimension(glycan, oligosaccharideWidth);

		BufferedImage image = new BufferedImage((int) dim.getWidth(), (int) dim
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(0, 0, dim.getWidth(), dim.getHeight()));
		Font oldFont = g2.getFont();
		Font newFont = oldFont.deriveFont(oldFont.getStyle(),
				((float) oligosaccharideWidth / 2));
		g2.setFont(newFont);

		builder.drawStructure(glycan, g2, oligosaccharideWidth, dim);

		FileOutputStream fos = new FileOutputStream(saveFile);
		try {
			ImageIO.write(image, "png", fos);
		} finally {
			fos.close();
		}

		return Arrays.asList(new String[] { saveFile });
	}

}
