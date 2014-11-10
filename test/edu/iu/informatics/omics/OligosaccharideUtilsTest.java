package edu.iu.informatics.omics;

import edu.iu.informatics.omics.impl.Hexose;
import junit.framework.TestCase;

public class OligosaccharideUtilsTest extends TestCase {

	public void testGetMaxDepth() {
		Glycan glycan;

		glycan = new Glycan("Test", "4-1ManNAc4-1Man");
		assertEquals(2, OligosaccharideUtils.getMaxDepth(glycan.getReducingTerm()));

		glycan = new Glycan("Test", "4-1ManNAc4-1Man(3-1ManNAc,4-2Neu5Ac)");
		assertEquals(3, OligosaccharideUtils.getMaxDepth(glycan.getReducingTerm()));

		glycan = new Glycan("Test", "4-1ManNAc4-1Man(3-1ManNAc4-1Man,4-2Neu5Ac)");
		assertEquals(4, OligosaccharideUtils.getMaxDepth(glycan.getReducingTerm()));

		glycan = new Glycan(
				"Test",
				"Man(4-1ManNAc4-1Man(3-1ManNAc4-1Man,4-1Neu5Ac),3-1ManNAc4-1Man4-1Neu5Ac,6-1Glc)");
		assertEquals(5, OligosaccharideUtils.getMaxDepth(glycan.getReducingTerm()));
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
	public void testInitializeNameSuffix() {
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
		
		OligosaccharideUtils.initializeNameSuffix(hex1);
		assertEquals("",OligosaccharideUtils.getNameSuffix(hex1));
		assertEquals("b",OligosaccharideUtils.getNameSuffix(hex2));
		assertEquals("a",OligosaccharideUtils.getNameSuffix(hex3));
		assertEquals("b\"",OligosaccharideUtils.getNameSuffix(hex4));
		assertEquals("b'",OligosaccharideUtils.getNameSuffix(hex5));
		assertEquals("a",OligosaccharideUtils.getNameSuffix(hex6));
		assertEquals("a\"",OligosaccharideUtils.getNameSuffix(hex7));
		assertEquals("a'",OligosaccharideUtils.getNameSuffix(hex8));
		assertEquals("a'",OligosaccharideUtils.getNameSuffix(hex9));
	}



}
