package edu.iu.informatics.omics.impl;

import junit.framework.TestCase;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class SimpleChemicalGroupTest extends TestCase {

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.SimpleChemicalGroup.parse(String)'
	 */
	public void testParse() {
		SimpleChemicalGroup cg = new SimpleChemicalGroup("CH2OH");
		assertEquals("COH3", cg.toString());
	}

	public void testParseException() {
		try {
			new SimpleChemicalGroup("CH2_OH");
			assertTrue("Should throw IllegalArgumentException", false);
		} catch (IllegalArgumentException ex) {
		}
	}

	public void testEquals(){
		SimpleChemicalGroup actual1 = new SimpleChemicalGroup("OH");
		assertEquals(ChemicalGroupFactory.OH, actual1);
		SimpleChemicalGroup actual2 = new SimpleChemicalGroup("HO");
		assertEquals(ChemicalGroupFactory.OH, actual2);
	}
	
	public void testHashCode(){
		SimpleChemicalGroup actual1 = new SimpleChemicalGroup("OH");
		assertEquals(ChemicalGroupFactory.OH.hashCode(), actual1.hashCode());
		SimpleChemicalGroup actual2 = new SimpleChemicalGroup("HO");
		assertEquals(ChemicalGroupFactory.OH.hashCode(), actual2.hashCode());
	}
}
