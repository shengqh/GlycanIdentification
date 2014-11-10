package edu.iu.informatics.omics.impl;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import junit.framework.TestCase;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;

public class MonoisotipicMassProxyTest extends TestCase {
	private IMassProxy massProxy;

	private MonoisotopicMassDefinitionImpl monoMassDifinition;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		massProxy = MassProxyFactory.getMassProxy(IsotopicType.Monoisotopic,
				DerivativeType.Underivatised, AdductType.H);
		monoMassDifinition = new MonoisotopicMassDefinitionImpl();
	}

	public void testGetProton() {
		massProxy.setAdduct(AdductType.H);
		assertEquals(monoMassDifinition.getProton(AdductType.H), massProxy
				.getAdduct());
		massProxy.setAdduct(AdductType.K);
		assertEquals(monoMassDifinition.getProton(AdductType.K), massProxy
				.getAdduct());
		massProxy.setAdduct(AdductType.Na);
		assertEquals(monoMassDifinition.getProton(AdductType.Na), massProxy
				.getAdduct());

	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.MonoisotipicMassProxy.getH2O()'
	 */
	public void testGetH2O() {
		assertEquals(monoMassDifinition.getAtom('H') * 2
				+ monoMassDifinition.getAtom('O'), massProxy.getH2O());
	}

}
