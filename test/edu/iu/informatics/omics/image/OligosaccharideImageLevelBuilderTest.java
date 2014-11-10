package edu.iu.informatics.omics.image;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.Fucose;
import edu.iu.informatics.omics.impl.Hexose;

public class OligosaccharideImageLevelBuilderTest extends TestCase {
	public void testAssignDrawDepth() {
		Glycan glycan = new Glycan();
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Fucose fuc3 = new Fucose();
		Hexose hex4 = new Hexose();
		Hexose hex5 = new Hexose();
		Hexose hex6 = new Hexose();
		Hexose hex7 = new Hexose();
		Hexose hex8 = new Hexose();

		hex2.linkToReducingTermMonosaccharide(1, 2, hex1);
		fuc3.linkToReducingTermMonosaccharide(1, 4, hex1);
		hex4.linkToReducingTermMonosaccharide(1, 6, hex1);
		hex5.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex6.linkToReducingTermMonosaccharide(1, 6, hex2);
		hex7.linkToReducingTermMonosaccharide(1, 4, hex4);
		hex8.linkToReducingTermMonosaccharide(1, 6, hex4);

		glycan.setReducingTerm(hex1);

		OligosaccharideImageLevelBuilder.assignDrawDepth(glycan);
		assertEquals(1, OligosaccharideImageLevelBuilder.getDepth(hex1));
		assertEquals(1, OligosaccharideImageLevelBuilder.getDepth(fuc3));
		assertEquals(2, OligosaccharideImageLevelBuilder.getDepth(hex2));
		assertEquals(2, OligosaccharideImageLevelBuilder.getDepth(hex4));
		assertEquals(3, OligosaccharideImageLevelBuilder.getDepth(hex5));
		assertEquals(3, OligosaccharideImageLevelBuilder.getDepth(hex6));
		assertEquals(3, OligosaccharideImageLevelBuilder.getDepth(hex7));
		assertEquals(3, OligosaccharideImageLevelBuilder.getDepth(hex8));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.image.OligosaccharideImageLevelBuilder.assignDrawLevel(Glycan)'
	 */
	public void testAssignDrawLevel() {
		Glycan glycan = new Glycan();
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Fucose fuc3 = new Fucose();
		Hexose hex4 = new Hexose();
		Hexose hex5 = new Hexose();
		Hexose hex6 = new Hexose();
		Hexose hex7 = new Hexose();
		Hexose hex8 = new Hexose();

		hex2.linkToReducingTermMonosaccharide(1, 2, hex1);
		fuc3.linkToReducingTermMonosaccharide(1, 4, hex1);
		hex4.linkToReducingTermMonosaccharide(1, 6, hex1);
		hex5.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex6.linkToReducingTermMonosaccharide(1, 6, hex2);
		hex7.linkToReducingTermMonosaccharide(1, 4, hex4);
		hex8.linkToReducingTermMonosaccharide(1, 6, hex4);

		glycan.setReducingTerm(hex1);

		OligosaccharideImageLevelBuilder.assignDrawLevel(glycan);
		assertEquals(6.0, OligosaccharideImageLevelBuilder.getLevel(hex5));
		assertEquals(5.0, OligosaccharideImageLevelBuilder.getLevel(hex2));
		assertEquals(4.0, OligosaccharideImageLevelBuilder.getLevel(hex6));
		assertEquals(2.5, OligosaccharideImageLevelBuilder.getLevel(fuc3));
		assertEquals(3.5, OligosaccharideImageLevelBuilder.getLevel(hex1));
		assertEquals(3.0, OligosaccharideImageLevelBuilder.getLevel(hex7));
		assertEquals(2.0, OligosaccharideImageLevelBuilder.getLevel(hex4));
		assertEquals(1.0, OligosaccharideImageLevelBuilder.getLevel(hex8));
	}

	public void testDeterminateFucoseDirection() {
		Glycan glycan = new Glycan("Test", "Man(3-1Fuc,6-1Gal,4-1Glc3-1Fuc)");

		OligosaccharideImageLevelBuilder.assignDrawDepth(glycan);

		List<IMonosaccharide> leaves = new ArrayList<IMonosaccharide>();

		OligosaccharideImageLevelBuilder.initializeLevel(leaves, null, 0, glycan
				.getReducingTerm());

		OligosaccharideImageLevelBuilder.determinateFucoseDirection(glycan);

		for (IMonosaccharide os : glycan.getOligosaccharides()) {
			if (os.getShortName().equals("Fuc")) {
				if (1 == OligosaccharideImageLevelBuilder.getDepth(os)) {
					assertEquals(-1.0, OligosaccharideImageLevelBuilder.getDirection(os));
				} else {
					assertEquals(1.0, OligosaccharideImageLevelBuilder.getDirection(os));
				}
			}
		}
	}

}
