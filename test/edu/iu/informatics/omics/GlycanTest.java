package edu.iu.informatics.omics;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import edu.iu.informatics.omics.impl.Hexose;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class GlycanTest extends TestCase {

	public void testIsLinear() {
		String expectSingleChain = "Glc6-1Glc6-1Glc";
		Glycan singleChainGlycan = new GlycanColFormat().parse(expectSingleChain);
		assertTrue(singleChainGlycan.isLinear());

		String expectDoubleChain = "Glc(3-1Man,4-1ManNAc)";
		Glycan doubleChainGlycan = new GlycanColFormat().parse(expectDoubleChain);
		assertFalse(doubleChainGlycan.isLinear());
	}

	public void testGetOligosaccharidesFromNonreducingTerm() {
		String expectSingleChain = "Glc6-1GlcNAc6-1Man";
		Glycan singleChainGlycan = new GlycanColFormat().parse(expectSingleChain);
		List<IMonosaccharide> nonreducing = singleChainGlycan
				.getOligosaccharidesFromNonreducingTerm();
		assertEquals("Man", nonreducing.get(0).getShortName());
		assertEquals("GlcNAc", nonreducing.get(1).getShortName());
		assertEquals("Glc", nonreducing.get(2).getShortName());
	}

	/**
	 * <code>
	 *          ----hex4
	 *          |
	 *   ------hex2
	 *   |      | 
	 *   |      ----hex5
	 *   |
	 * hex1
	 *   |             ----hex7
	 *   |             |
	 *   ------hex3--hex6
	 *                 |
	 *                 ----hex8--hex9
	 *</code>
	 */
	public void testGetOligosaccharidesFromReducingTerm() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();
		Hexose hex5 = new Hexose();
		Hexose hex6 = new Hexose();
		Hexose hex7 = new Hexose();
		Hexose hex8 = new Hexose();
		Hexose hex9 = new Hexose();

		hex2.linkToReducingTermMonosaccharide(1, 4, hex1);
		hex3.linkToReducingTermMonosaccharide(1, 6, hex1);
		hex4.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex5.linkToReducingTermMonosaccharide(1, 6, hex2);
		hex6.linkToReducingTermMonosaccharide(1, 6, hex3);
		hex7.linkToReducingTermMonosaccharide(1, 4, hex6);
		hex8.linkToReducingTermMonosaccharide(1, 6, hex6);
		hex9.linkToReducingTermMonosaccharide(1, 6, hex8);

		Glycan glycan = new Glycan();
		glycan.setReducingTerm(hex1);

		List<IMonosaccharide> mss = glycan.getOligosaccharidesFromReducingTerm();
		assertSame(hex1, mss.get(0));
		assertSame(hex3, mss.get(1));
		assertSame(hex6, mss.get(2));
		assertSame(hex8, mss.get(3));
		assertSame(hex9, mss.get(4));
		assertSame(hex7, mss.get(5));
		assertSame(hex2, mss.get(6));
		assertSame(hex5, mss.get(7));
		assertSame(hex4, mss.get(8));
	}
	
	public void testGetChildrenParentMap() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		Hexose hex4 = new Hexose();
		Hexose hex5 = new Hexose();

		hex2.linkToReducingTermMonosaccharide(1, 4, hex1);
		hex3.linkToReducingTermMonosaccharide(1, 6, hex1);
		hex4.linkToReducingTermMonosaccharide(1, 4, hex2);
		hex5.linkToReducingTermMonosaccharide(1, 6, hex2);

		Glycan glycan = new Glycan();
		glycan.setReducingTerm(hex1);
		Map<IMonosaccharide, IMonosaccharide> map = glycan.getChildrenParentMap();
		assertSame(hex1, map.get(hex2));
		assertSame(hex1, map.get(hex3));
		assertSame(hex2, map.get(hex4));
		assertSame(hex2, map.get(hex5));
	}

	public void testGetAtomCounts() {
		Glycan glycan = new GlycanColFormat().parse("Glc6-1Man");
		Map<Character, Integer> acs = glycan.getAtomCounts();
		assertEquals(11, acs.get('O').intValue());
		assertEquals(22, acs.get('H').intValue());
		assertEquals(12, acs.get('C').intValue());
	}

	public void testGetAtomCompositionStr() {
		Glycan glycan = new GlycanColFormat().parse("Glc6-1Man");
		assertEquals("C12H22O11", glycan.getAtomCompositionStr());
	}

}
