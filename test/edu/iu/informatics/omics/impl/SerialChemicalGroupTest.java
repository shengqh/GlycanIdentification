package edu.iu.informatics.omics.impl;

import junit.framework.TestCase;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class SerialChemicalGroupTest extends TestCase {

	public void testContainSubGroup(){
		SerialChemicalGroup group = new SerialChemicalGroup(ChemicalGroupFactory
				.getCH2(), ChemicalGroupFactory.getCHOH());
		assertTrue(group.containSubGroup(ChemicalGroupFactory.OH));
	}
	
	public void testReplace() {
		SerialChemicalGroup group = new SerialChemicalGroup(ChemicalGroupFactory
				.getCH2(), ChemicalGroupFactory.OH);
		assertEquals("CH2OH", group.toString());

		group.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.getCH3());
		assertEquals("CH2CH3", group.toString());
	}
	
	public void testRemove() {
		SerialChemicalGroup group = new SerialChemicalGroup(ChemicalGroupFactory
				.getCH2(), ChemicalGroupFactory.OH);
		group.removeFirstSubGroup(ChemicalGroupFactory.OH);
		
		assertEquals("CH2", group.toString());
	}

}
