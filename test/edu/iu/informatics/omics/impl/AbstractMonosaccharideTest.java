package edu.iu.informatics.omics.impl;

import java.util.Map;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMonosaccharide;

public class AbstractMonosaccharideTest extends TestCase {
	private IMonosaccharide hex1;

	private IMonosaccharide hex2;

	private IMonosaccharide hex3;

	@Override
	protected void setUp() {
		hex1 = new Hexose();
		hex2 = new Hexose();
		hex3 = new Hexose();
		hex2.linkToReducingTermMonosaccharide(1, 3, hex1);
		hex3.linkToReducingTermMonosaccharide(1, 4, hex2);
	}

	public void testGetSubMonosaccharides() {
		Map<Integer, IMonosaccharide> subMos = hex2.getSubMonosaccharides();
		assertEquals(1, subMos.size());
		assertEquals(4, subMos.keySet().iterator().next().intValue());
		assertEquals(hex3, subMos.values().iterator().next());
	}

	public void testGetMonosaccharides() {
		Map<Integer, IMonosaccharide> mos = hex2.getMonosaccharides();
		assertEquals(2, mos.size());
		assertEquals(hex1, mos.get(1));
		assertEquals(hex3, mos.get(4));
	}

	public void testIsReducingTerm() {
		assertTrue(hex1.isReducingTerm());
		assertFalse(hex2.isReducingTerm());
		assertFalse(hex3.isReducingTerm());
	}

	public void testIsNonreducingTerm() {
		assertFalse(hex1.isNonreducingTerm());
		assertFalse(hex2.isNonreducingTerm());
		assertTrue(hex3.isNonreducingTerm());
	}

	public void testGetMaxDistanceToLeaf() {
		Glycan glycan = new Glycan(
				"Test",
				"Man(4-1ManNAc4-1Man(3-1ManNAc4-1Man,4-1Neu5Ac),3-1ManNAc4-1Man4-1Neu5Ac,6-1Glc)");

		assertEquals(5, glycan.getReducingTerm().getMaxDistanceToLeaf());

		Map<Integer, IMonosaccharide> mss = glycan.getReducingTerm()
				.getSubMonosaccharides();
		assertEquals(3, mss.get(3).getMaxDistanceToLeaf());
		assertEquals(4, mss.get(4).getMaxDistanceToLeaf());
		assertEquals(1, mss.get(6).getMaxDistanceToLeaf());
	}

}
