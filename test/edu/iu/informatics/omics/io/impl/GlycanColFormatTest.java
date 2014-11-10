package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.util.regex.Matcher;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;

public class GlycanColFormatTest extends TestCase {
	private GlycanColFormat gcf = new GlycanColFormat();

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.getNodePattern()'
	 */
	public void testGetNodePattern() {
		Matcher matcher = GlycanColFormat.getNodePattern().matcher(
				"R-1Glc4-1Man(6-1Glc,6-1Glc4-1Man)");
		assertTrue(matcher.find());
		assertEquals("R", matcher.group(1));
		assertEquals("1", matcher.group(2));
		assertEquals("Glc", matcher.group(3));
		assertEquals("4-1Man(6-1Glc,6-1Glc4-1Man)", matcher.group(4));

		Matcher matcher2 = GlycanColFormat.getNodePattern().matcher(
				"Glc4-1Man(6-1Glc,6-1Glc4-1Man)");
		assertTrue(matcher2.find());
		assertNull(matcher2.group(1));
		assertNull(matcher2.group(2));
		assertEquals("Glc", matcher2.group(3));
		assertEquals("4-1Man(6-1Glc,6-1Glc4-1Man)", matcher2.group(4));

		Matcher matcher3 = GlycanColFormat.getNodePattern().matcher(
		"6-1Neu5Ac6-1Glc");
		assertTrue(matcher3.find());
		assertEquals("6",matcher3.group(1));
		assertEquals("1",matcher3.group(2));
		assertEquals("Neu5Ac", matcher3.group(3));
		assertEquals("6-1Glc", matcher3.group(4));

		Matcher matcher4 = GlycanColFormat.getNodePattern().matcher(
				"(6-1Glc,6-1Glc4-1Man)");
		assertFalse(matcher4.find());
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.parse(String)'
	 */
	public void testParse() {
		String expectSingleChain = "Glc6-1Neu5Ac6-1Glc6-1Glc6-1Glc";
		Glycan singleChainGlycan = gcf.parse(expectSingleChain);
		assertEquals(expectSingleChain, gcf.glycanToString(singleChainGlycan));

		String expectDoubleChain = "ManNAc6-1Glc(3-1Man6-1Glc(3-1Gal,4-1Man),4-1ManNAc)";
		Glycan doubleChainGlycan = gcf.parse(expectDoubleChain);
		assertEquals(expectDoubleChain, gcf.glycanToString(doubleChainGlycan));

		try {
			gcf.parse("ManNAc(3-1Man(3ManNAc,4Man)");
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
		}
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.read(String)'
	 */
	public void testRead() throws IOException {
		Glycan branchGlycan = gcf.read("data/branch_col.inp");
		assertEquals("ManNAc6-1Glc(3-1Man6-1Glc(3-1Gal,4-1Man),4-1ManNAc)", gcf
				.glycanToString(branchGlycan));
		assertFalse(branchGlycan.isLinear());

		Glycan singleChainGlycan = gcf.read("data/Dextran_glc06_col.inp");
		assertEquals("Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc", gcf
				.glycanToString(singleChainGlycan));
		assertTrue(singleChainGlycan.isLinear());
	}

	public void testGlycanToString() {
		String expectDoubleChain = "ManNAc6-1Glc(3-1Man6-1Glc(3-1Gal,4-1Man),4-1ManNAc)";
		Glycan doubleChainGlycan = gcf.parse(expectDoubleChain);
		assertEquals(expectDoubleChain, gcf.glycanToString(doubleChainGlycan));
	}
	
	public void testParse2(){
		String expectChain = "GlcNAc4-1GlcNAc4-1Man(3-1Man(2-1Man2-1Man,6-1Man),4-1GlcNAc,6-1Man3-1Man2-1Man)";
		Glycan chainGlycan = gcf.parse(expectChain);
		assertEquals(expectChain, gcf.glycanToString(chainGlycan));
	}

}
