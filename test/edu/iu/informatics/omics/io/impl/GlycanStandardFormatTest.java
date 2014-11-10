package edu.iu.informatics.omics.io.impl;

import junit.framework.TestCase;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMonosaccharide;

public class GlycanStandardFormatTest extends TestCase {

	private GlycanStandardFormat gsf = new GlycanStandardFormat();
	
	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.io.impl.GlycanStandardFormat.parse(String)'
	 */
	public void testParseString() {
		Glycan g = gsf
				.parse("Gal1-6Glc1-4(Man1-4GlcNAc1-6)[Fuc1-2]Neu5Ac1-2Man");

		IMonosaccharide man = g.getReducingTerm();
		assertEquals("Man", man.getShortName());

		IMonosaccharide neuAc = man.getMonosaccharides().get(2);
		assertNotNull(neuAc);
		assertEquals("Neu5Ac", neuAc.getShortName());

		IMonosaccharide glc14 = neuAc.getMonosaccharides().get(4);
		assertNotNull(glc14);
		assertEquals("Glc", glc14.getShortName());

		IMonosaccharide gal16 = glc14.getMonosaccharides().get(6);
		assertNotNull(gal16);
		assertEquals("Gal", gal16.getShortName());

		IMonosaccharide glcNAc16 = neuAc.getMonosaccharides().get(6);
		assertNotNull(glcNAc16);
		assertEquals("GlcNAc", glcNAc16.getShortName());

		IMonosaccharide man14 = glcNAc16.getMonosaccharides().get(4);
		assertNotNull(man14);
		assertEquals("Man", man14.getShortName());

		IMonosaccharide fuc12 = neuAc.getMonosaccharides().get(2);
		assertNotNull(fuc12);
		assertEquals("Fuc", fuc12.getShortName());
	}
	
	public void testGlycanToString() {
		String expectDoubleChain = "Gal1-6Glc1-4(Man1-4GlcNAc1-6)[Fuc1-2]Neu5Ac1-2Man";
		Glycan doubleChainGlycan = gsf.parse(expectDoubleChain);
		assertEquals(expectDoubleChain, gsf.glycanToString(doubleChainGlycan));
	}
	

}
