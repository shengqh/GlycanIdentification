package edu.iu.informatics.omics.impl;

import java.util.HashSet;

import junit.framework.TestCase;
import cn.ac.rcpa.chem.AtomCompositionUtils;
import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class AbstractChemicalGroupTest extends TestCase {

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.containSubGroup(ChemicalGroup)'
	 */
	public void testContainSubGroupChemicalGroup() {
		PararrelChemicalGroup pg = new PararrelChemicalGroup(
				ChemicalGroupFactory.OH, ChemicalGroupFactory.getCH3());
		assertTrue(pg.containSubGroup(ChemicalGroupFactory.OH));

		SerialChemicalGroup sg = new SerialChemicalGroup(ChemicalGroupFactory.C, pg);
		assertTrue(sg.containSubGroup(ChemicalGroupFactory.OH));

		PararrelChemicalGroup pg2 = new PararrelChemicalGroup(
				ChemicalGroupFactory.C, sg);
		assertTrue(pg2.containSubGroup(ChemicalGroupFactory.OH));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.containSubGroup(int,
	 * ChemicalGroup)'
	 */
	public void testContainSubGroupIntChemicalGroup() {
		Hexose hex = new Hexose();
		assertTrue(hex.containSubGroup(1, ChemicalGroupFactory.OH));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.getLinkMass(MassDefinition,
	 * int, Set<ChemicalGroup>)'
	 */
	public void testGetLinkMassMassDefinitionIntSetOfChemicalGroup() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(1, hex3);

		HashSet<ChemicalGroup> excepts = new HashSet<ChemicalGroup>();
		AverageMassDefinitionImpl md = new AverageMassDefinitionImpl();

		assertEquals(360.31536, hex1.getLinkMass(md, 1, excepts), 0.1);

		excepts.add(hex3);
		assertEquals(180.15768, hex1.getLinkMass(md, 1, excepts));

		excepts.add(hex2);
		excepts.remove(hex3);
		assertEquals(0.0, hex1.getLinkMass(md, 1, excepts));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.getLinkMass(MassDefinition,
	 * int)'
	 */
	public void testGetLinkMassMassDefinitionInt() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(1, hex3);

		AverageMassDefinitionImpl md = new AverageMassDefinitionImpl();

		assertEquals(360.31536, hex1.getLinkMass(md, 1), 0.1);
		assertEquals(180.15768, hex2.getLinkMass(md, 1), 0.1);
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.getMass(MassDefinition)'
	 */
	public void testGetMass() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(1, hex3);

		AverageMassDefinitionImpl md = new AverageMassDefinitionImpl();

		assertEquals(180.15768, hex1.getMass(md), 0.1);
		assertEquals(180.15768, hex2.getMass(md), 0.1);
		assertEquals(180.15768, hex3.getMass(md), 0.1);
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.getMassAll(MassDefinition,
	 * Set<ChemicalGroup>)'
	 */
	public void testGetMassAll() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(4, hex1);
		hex2.setLink(1, hex3);
		hex3.setLink(6, hex2);

		HashSet<ChemicalGroup> excepts = new HashSet<ChemicalGroup>();
		AverageMassDefinitionImpl md = new AverageMassDefinitionImpl();

		assertEquals(540.47304, hex1.getMassAll(md, excepts), 0.1);
		assertEquals(540.47304, hex2.getMassAll(md, excepts), 0.1);
		assertEquals(540.47304, hex3.getMassAll(md, excepts), 0.1);
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.removeFirstSubGroup(ChemicalGroup)'
	 */
	public void testRemoveFirstSubGroupChemicalGroup() {
		Hexose hex1 = new Hexose();
		assertEquals(ChemicalGroupFactory.getCHOH(), hex1.getSubGroups().get(1));
		hex1.removeFirstSubGroup(1, ChemicalGroupFactory.OH);
		assertEquals(ChemicalGroupFactory.getCH(), hex1.getSubGroups().get(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.removeFirstSubGroup(int,
	 * ChemicalGroup)'
	 */
	public void testRemoveFirstSubGroupIntChemicalGroup() {
		Hexose hex1 = new Hexose();
		assertEquals(ChemicalGroupFactory.getCHOH(), hex1.getSubGroups().get(1));
		hex1.removeFirstSubGroup(1, ChemicalGroupFactory.OH);
		assertEquals(ChemicalGroupFactory.getCH(), hex1.getSubGroups().get(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.removeLink(int)'
	 */
	public void testRemoveLink() {
		Hexose hex1 = new Hexose();
		hex1.setLink(1, ChemicalGroupFactory.OAc);
		assertEquals(ChemicalGroupFactory.OAc, hex1.getLinks().get(1));

		hex1.removeLink(1);
		assertEquals(null, hex1.getLinks().get(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.replaceAll(ChemicalGroup,
	 * ChemicalGroup, Set<ChemicalGroup>)'
	 */
	public void testReplaceAllChemicalGroupChemicalGroupSetOfChemicalGroup() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(4, hex1);
		hex2.setLink(1, hex3);
		hex3.setLink(6, hex2);

		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));

		HashSet<ChemicalGroup> excepts = new HashSet<ChemicalGroup>();
		excepts.add(hex2);
		hex1.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.N, excepts);
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));

		excepts.remove(hex2);
		excepts.add(hex3);
		hex1.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.N, excepts);
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));

		excepts.remove(hex3);
		hex1.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.N, excepts);
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.replaceAll(ChemicalGroup,
	 * ChemicalGroup)'
	 */
	public void testReplaceAllChemicalGroupChemicalGroup() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		Hexose hex3 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(4, hex1);
		hex2.setLink(1, hex3);
		hex3.setLink(6, hex2);

		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));

		hex1.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.N);
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex3
				.getAtomCounts()));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.replaceAllSubGroup(ChemicalGroup,
	 * ChemicalGroup)'
	 */
	public void testReplaceAllSubGroup() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		hex1.setLink(1, hex2);
		hex2.setLink(4, hex1);

		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));

		hex1.replaceAllSubGroup(ChemicalGroupFactory.OH, ChemicalGroupFactory.N);
		assertEquals("C6H7N5O", AtomCompositionUtils.mapToString(hex1
				.getAtomCounts()));
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex2
				.getAtomCounts()));

	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.replaceFirstSubGroup(ChemicalGroup,
	 * ChemicalGroup)'
	 */
	public void testReplaceFirstSubGroupChemicalGroupChemicalGroup() {
		Hexose hex1 = new Hexose();
		assertEquals(ChemicalGroupFactory.getCHOH(), hex1.getSubGroups().get(1));

		hex1.replaceFirstSubGroup(ChemicalGroupFactory.OH, ChemicalGroupFactory.N);
		ChemicalGroup expect = new SerialChemicalGroup(ChemicalGroupFactory.C,
				new PararrelChemicalGroup(ChemicalGroupFactory.H,
						ChemicalGroupFactory.N));
		assertEquals(expect, hex1.getSubGroups().get(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.replaceFirstSubGroup(int,
	 * ChemicalGroup, ChemicalGroup)'
	 */
	public void testReplaceFirstSubGroupIntChemicalGroupChemicalGroup() {
		Hexose hex1 = new Hexose();
		assertEquals(ChemicalGroupFactory.getCHOH(), hex1.getSubGroups().get(2));

		hex1.replaceFirstSubGroup(2, ChemicalGroupFactory.OH,
				ChemicalGroupFactory.N);
		ChemicalGroup expect = new SerialChemicalGroup(ChemicalGroupFactory.C,
				new PararrelChemicalGroup(ChemicalGroupFactory.H,
						ChemicalGroupFactory.N));
		assertEquals(expect, hex1.getSubGroups().get(2));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.AbstractChemicalGroup.setLink(int,
	 * ChemicalGroup)'
	 */
	public void testSetLink() {
		Hexose hex1 = new Hexose();
		Hexose hex2 = new Hexose();
		hex1.setLink(1, hex2);

		assertEquals(hex2, hex1.getLinks().get(1));
	}
}
