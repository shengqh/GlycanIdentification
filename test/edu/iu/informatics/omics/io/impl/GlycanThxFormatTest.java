package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.util.regex.Matcher;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;

public class GlycanThxFormatTest extends TestCase {
	private GlycanThxFormat gtf = new GlycanThxFormat();

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.getNodePattern()'
	 */
	public void testGetNodePattern() {
		Matcher matcher = GlycanThxFormat.getNodePattern().matcher(
				"Glc(4Man(6Glc,6Glc(4Man)))");
		assertTrue(matcher.find());
		assertEquals("", matcher.group(1));
		assertEquals("Glc", matcher.group(2));

		Matcher matcher2 = GlycanThxFormat.getNodePattern().matcher(
				"4Man(6Glc,6Glc(4Man))");
		assertTrue(matcher2.find());
		assertEquals("4", matcher2.group(1));
		assertEquals("Man", matcher2.group(2));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.parse(String)'
	 */
	public void testParse() {
		String expectSingleChain = "Glc(6Glc(6Glc(6Glc(6Glc))))";
		Glycan singleChainGlycan = gtf.parse(expectSingleChain);
		assertEquals(expectSingleChain, gtf.glycanToString(singleChainGlycan));

		String expectDoubleChain = "ManNAc(6Glc(3Man(4ManNAc,6Glc(3Gal,4Man))))";
		Glycan doubleChainGlycan = gtf.parse(expectDoubleChain);
		assertEquals(expectDoubleChain, gtf.glycanToString(doubleChainGlycan));

		try {
			gtf.parse("ManNAc(3Man(3ManNAc,4Man)");
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
		}
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanColFormatReader.read(String)'
	 */
	public void testRead() throws IOException {
		Glycan branchGlycan = gtf.read("data/branch_thx.inp");
		assertEquals("ManNAc(6Glc(3Man(6Glc(3Gal,4Man)),4ManNAc))", gtf
				.glycanToString(branchGlycan));
		assertFalse(branchGlycan.isLinear());

		Glycan singleChainGlycan = gtf.read("data/Dextran_glc06_thx.inp");
		assertEquals("Glc(6Glc(6Glc(6Glc(6Glc(6Glc)))))", gtf
				.glycanToString(singleChainGlycan));
		assertTrue(singleChainGlycan.isLinear());
	}

	public void testGlycanToString() {
		String expectDoubleChain = "ManNAc(6Glc(3Man(4ManNAc,6Glc(3Gal,4Man))))";
		Glycan doubleChainGlycan = gtf.parse(expectDoubleChain);
		assertEquals(expectDoubleChain, gtf.glycanToString(doubleChainGlycan));
	}

}
