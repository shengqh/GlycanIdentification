package edu.iu.informatics.omics;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import junit.framework.TestCase;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class GlycanUtilsTest extends TestCase {
	public void testConvertToLinkage() {
		String oldGlycanStr = "4-1ManNAc4-1Man(3-1ManNAc,4-2Neu5Ac)";
		String newGlycanStr = GlycanUtils.convertToLinkage(oldGlycanStr, 6);
		assertEquals("6-1ManNAc6-1Man(6-1ManNAc,6-2Neu5Ac)", newGlycanStr);
	}

	public void testReadAs() throws IOException {
		IGlycanReader reader = new GlycanColFormat();

		String filename = "data/glycanutils_test.inp";

		Glycan originalGlycan = reader.read(filename);
		assertEquals("ManNAc4-1Man3-1ManNAc4-2Neu5Ac", reader
				.glycanToString(originalGlycan));

		Glycan glycan6 = GlycanUtils.readAs(reader, filename, 6);

		assertEquals("ManNAc6-1Man6-1ManNAc6-2Neu5Ac", reader
				.glycanToString(glycan6));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanUtils.getSubTreePattern()'
	 */
	public void testGetSubTreePattern() {
		Matcher matcher = GlycanUtils.getSubTreePattern().matcher(
				"(6-1Glc,6-1Glc4-1Man)");
		assertTrue(matcher.find());
		assertEquals("6-1Glc,6-1Glc4-1Man", matcher.group(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanUtils.getBranches(String)'
	 */
	public void testGetBranchesOfGlycanGolFormat() {
		List<String> actual1 = GlycanUtils.getBranches("(6-1Glc)");
		assertEquals("6-1Glc", actual1.get(0));

		List<String> actual3 = GlycanUtils.getBranches("Glc(6-1Glc)");
		assertEquals("6-1Glc", actual3.get(0));

		List<String> actual2 = GlycanUtils
				.getBranches("Man(4-1ManNAc4-1Man(3-1ManNAc,4-1NeuAc),3-1ManNAc4-1Man4-1NeuAc,6-1Glc)");
		assertEquals(3, actual2.size());
		assertEquals("4-1ManNAc4-1Man(3-1ManNAc,4-1NeuAc)", actual2.get(0));
		assertEquals("3-1ManNAc4-1Man4-1NeuAc", actual2.get(1));
		assertEquals("6-1Glc", actual2.get(2));
	}

	public void testGetBranchesOfGlycanThxFormat() {
		List<String> actual1 = GlycanUtils.getBranches("(6Glc)");
		assertEquals("6Glc", actual1.get(0));

		List<String> actual2 = GlycanUtils
				.getBranches("(4ManNAc(4Man(3ManNAc(4NeuAc))),3ManNAc(4Man(4NeuAc)),6Glc)");
		assertEquals(3, actual2.size());
		assertEquals("4ManNAc(4Man(3ManNAc(4NeuAc)))", actual2.get(0));
		assertEquals("3ManNAc(4Man(4NeuAc))", actual2.get(1));
		assertEquals("6Glc", actual2.get(2));
	}

	public void testGetBranch() {
		assertEquals("Glc1-6Glc1-4", GlycanUtils.getBranch("Glc1-6Glc1-4(Man1-4Glc1-6)NeuAc1-2Man"));
		assertEquals("Man1-4Glc1-6", GlycanUtils.getBranch("(Man1-4Glc1-6)NeuAc1-2Man"));
		assertEquals("NeuAc1-2Man", GlycanUtils.getBranch("NeuAc1-2Man"));
	}
	
}
