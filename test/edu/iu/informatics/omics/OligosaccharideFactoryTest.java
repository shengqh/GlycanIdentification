package edu.iu.informatics.omics;

import junit.framework.TestCase;
import cn.ac.rcpa.chem.AtomCompositionUtils;

public class OligosaccharideFactoryTest extends TestCase {

	public void testCreateHex() {
		IMonosaccharide hex = MonosaccharideFactory.create("Hex");
		assertEquals("C6H12O6", AtomCompositionUtils.mapToString(hex
				.getAtomCounts()));
	}

	public void testCreateHexNAc() {
		IMonosaccharide hex = MonosaccharideFactory.create("HexNAc");
		assertEquals("C8H15NO6", AtomCompositionUtils.mapToString(hex
				.getAtomCounts()));
	}

	public void testCreateXyl() {
		IMonosaccharide xyl = MonosaccharideFactory.create("Xyl");
		assertEquals("C5H10O5", AtomCompositionUtils.mapToString(xyl
				.getAtomCounts()));
	}

	public void testCreateNeu5Ac() {
		IMonosaccharide neuNAc = MonosaccharideFactory.create("Neu5Ac");
		assertEquals("C11H19NO9", AtomCompositionUtils.mapToString(neuNAc
				.getAtomCounts()));
	}

}
