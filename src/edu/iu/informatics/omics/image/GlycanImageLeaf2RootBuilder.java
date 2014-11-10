package edu.iu.informatics.omics.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.Fucose;

public class GlycanImageLeaf2RootBuilder extends AbstractGlycanImageBuilder {
	@Override
	public void drawStructure(Glycan glycan, Graphics2D g2,
			int oligosaccharideWidth, Dimension dim) {
		drawOligosaccharide(g2, dim.width - oligosaccharideWidth * 3 / 2, oligosaccharideWidth / 2,
				glycan.getReducingTerm(), oligosaccharideWidth, true);
	}

	private void drawOligosaccharide(Graphics2D g2, int currentX, int topY,
			IMonosaccharide os, int oligosaccharideWidth, boolean isRoot) {
		int levelHeight = oligosaccharideWidth * 2;
		int depthWidth = oligosaccharideWidth * 2;

		int width = levelHeight / 2;
		IOligosaccharideDrawer drawer = OligosaccharideDrawerFactory.create(os,
				oligosaccharideWidth);
		double curLevel = OligosaccharideImageLevelBuilder.getLevel(os);
		int currentY = (int) (topY + (curLevel - 1) * levelHeight);

		Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		oss.remove(os.getParentReducingTermPosition());

		int fontWidth = g2.getFontMetrics().charWidth('M');
		int fontHeight = g2.getFontMetrics().getHeight();

		for (Integer subInParentPosition : oss.keySet()) {
			IMonosaccharide subOs = oss.get(subInParentPosition);

			boolean isFucuse = subOs instanceof Fucose;

			int nextX = isFucuse ? currentX : currentX - depthWidth;

			double subLevel = OligosaccharideImageLevelBuilder.getLevel(subOs);

			int nextY = (int) (topY + (subLevel - 1) * levelHeight);

			g2.setColor(Color.BLACK);

			g2.drawLine(currentX + width / 2, currentY + width / 2,
					nextX + width / 2, nextY + width / 2);

			if (isFucuse) {
				Integer parentInSubPosition = subOs.getParentReducingTermPosition();

				int xParentPosition = (int) (currentX + width / 2 + 0.5 * fontWidth);
				int yParentPosition;

				int xSubPosition = xParentPosition;
				int ySubPosition;
				if (currentY > nextY) {
					yParentPosition = currentY;
					ySubPosition = (int) (nextY + oligosaccharideWidth / 2 + fontHeight * 1.5);
				} else {
					yParentPosition = (int) (currentY + oligosaccharideWidth / 2 + fontHeight * 1.5);
					ySubPosition = nextY;
				}
				g2.drawString(subInParentPosition.toString(), xParentPosition,
						yParentPosition);

				g2.drawString(parentInSubPosition.toString(), xSubPosition,
						ySubPosition);

				drawOligosaccharide(g2, currentX, topY, subOs, oligosaccharideWidth,
						false);
			} else {
				Integer parentPosition = subOs.getParentReducingTermPosition();

				int xParentPosition;
				int yParentPosition;
				int xSubPosition = nextX + oligosaccharideWidth;
				int ySubPosition = 0;
				if (currentY == nextY) {
					xParentPosition = currentX - fontWidth;
					yParentPosition = currentY + oligosaccharideWidth / 2 - 2;

					ySubPosition = currentY + oligosaccharideWidth / 2 - 2;
				} else if (currentY > nextY) {
					xParentPosition = currentX + oligosaccharideWidth / 4;
					yParentPosition = currentY;

					ySubPosition = nextY + oligosaccharideWidth;
				} else {
					xParentPosition = currentX + oligosaccharideWidth /4;
					yParentPosition = currentY + oligosaccharideWidth + width / 3;

					ySubPosition = nextY + oligosaccharideWidth / 2;
				}
				g2.drawString(subInParentPosition.toString(), xParentPosition,
						yParentPosition);

				g2.drawString(parentPosition.toString(), xSubPosition, ySubPosition);

				drawOligosaccharide(g2, nextX, topY, subOs, oligosaccharideWidth, false);
			}
		}
		drawer.draw(g2, currentX, currentY);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Leaf to Root Image Builder";
	}

}
