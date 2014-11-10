package edu.iu.informatics.omics.impl;

import junit.framework.TestCase;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class ParallelChemicalGroupTest extends TestCase {

	public void testReplace() {
		PararrelChemicalGroup group = new PararrelChemicalGroup(
				ChemicalGroupFactory.OH, ChemicalGroupFactory.getCH3());

		assertEquals("COH4", group.toString());

		group.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.getCH3());
		assertEquals("C2H6", group.toString());
	}

	public void testContainSubGroup() {
		PararrelChemicalGroup group = new PararrelChemicalGroup(
				ChemicalGroupFactory.OH, ChemicalGroupFactory.getCH3());
		assertTrue(group.containSubGroup(ChemicalGroupFactory.OH));
	}

}
