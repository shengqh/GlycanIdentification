package edu.iu.informatics.omics.impl;

import junit.framework.TestCase;
import cn.ac.rcpa.chem.AtomCompositionUtils;
import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.IMassDefinition;

public class HexoseTest extends TestCase {
	public void testHexoseResidue() {
		Hexose hex = new Hexose();
		IMassDefinition md = new AverageMassDefinitionImpl();
		assertTrue(hex.replaceFirstSubGroup(6, ChemicalGroupFactory.OH,
				ChemicalGroupFactory.O));
		assertTrue(hex.removeFirstSubGroup(1, ChemicalGroupFactory.OH));
		assertEquals("C6H10O5", AtomCompositionUtils.mapToString(hex.getAtomCounts()));
		assertEquals(162.1424, hex.getMass(md), 0.1);
	}

	public void testHexoseResiduePermethylated() {
		Hexose hex = new Hexose();
		IMassDefinition md = new AverageMassDefinitionImpl();
		hex
				.replaceFirstSubGroup(6, ChemicalGroupFactory.OH,
						ChemicalGroupFactory.O);
		hex.removeFirstSubGroup(1, ChemicalGroupFactory.OH);

		hex.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.OMe);
		assertEquals(204.2230, hex.getMass(md), 0.1);
		assertEquals("C9H16O5", AtomCompositionUtils.mapToString(hex.getAtomCounts()));
	}

	public void testHexoseResiduePeracetylated() {
		Hexose hex = new Hexose();
		IMassDefinition md = new AverageMassDefinitionImpl();
		hex
				.replaceFirstSubGroup(6, ChemicalGroupFactory.OH,
						ChemicalGroupFactory.O);
		hex.removeFirstSubGroup(1, ChemicalGroupFactory.OH);

		hex.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.OAc);
		assertEquals(288.2542, hex.getMass(md), 0.1);
		assertEquals("C12H16O8", AtomCompositionUtils.mapToString(hex.getAtomCounts()));
	}

	public void testHexoseChain() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		IMassDefinition md = new AverageMassDefinitionImpl();
		hex1.linkToReducingTermMonosaccharide(1, 6, hex2);
		hex2.linkToReducingTermMonosaccharide(1, 6, hex3);
		assertEquals(163.1414, hex1.getMass(md), 0.1);
		assertEquals("C6H11O5", AtomCompositionUtils.mapToString(hex1.getAtomCounts()));
		assertEquals(162.1414, hex2.getMass(md), 0.1);
		assertEquals("C6H10O5", AtomCompositionUtils.mapToString(hex2.getAtomCounts()));
		assertEquals(179.14974, hex3.getMass(md), 0.1);
		assertEquals("C6H11O6", AtomCompositionUtils.mapToString(hex3.getAtomCounts()));

		assertEquals(341.29, hex1.getLinkMass(md, 1), 0.1);
		assertEquals(179.14, hex2.getLinkMass(md, 1), 0.1);
		assertEquals(163.15, hex2.getLinkMass(md, 6), 0.1);
		assertEquals(325.29, hex3.getLinkMass(md, 6), 0.1);
	}

	public void testGetReducingTermBranch() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();

		hex1.linkToReducingTermMonosaccharide(1, 2, hex2);
		hex3.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex4.linkToReducingTermMonosaccharide(2, 4, hex3);

		assertSame(hex2, hex1.getRoot());
		assertSame(hex2, hex2.getRoot());
		assertSame(hex2, hex3.getRoot());
		assertSame(hex2, hex4.getRoot());
	}

	public void testGetReducingTermLinear() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();

		hex2.linkToReducingTermMonosaccharide(1, 4, hex1);
		hex3.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex4.linkToReducingTermMonosaccharide(2, 4, hex3);

		assertSame(hex1, hex1.getRoot());
		assertSame(hex1, hex2.getRoot());
		assertSame(hex1, hex3.getRoot());
		assertSame(hex1, hex4.getRoot());
	}

	public void testGetIndexFromReducing() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();

		hex1.linkToReducingTermMonosaccharide(1, 2, hex2);
		hex3.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex4.linkToReducingTermMonosaccharide(2, 4, hex3);

		assertEquals(1, hex2.getDistanceToRoot());
		assertEquals(2, hex1.getDistanceToRoot());
		assertEquals(2, hex3.getDistanceToRoot());
		assertEquals(3, hex4.getDistanceToRoot());
	}

	public void testGetIndexFromNonReducing() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();

		hex1.linkToReducingTermMonosaccharide(1, 2, hex2);
		hex3.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex4.linkToReducingTermMonosaccharide(2, 4, hex3);

		assertEquals(3, hex2.getMaxDistanceToLeaf());
		assertEquals(1, hex1.getMaxDistanceToLeaf());
		assertEquals(2, hex3.getMaxDistanceToLeaf());
		assertEquals(1, hex4.getMaxDistanceToLeaf());
	}

}
