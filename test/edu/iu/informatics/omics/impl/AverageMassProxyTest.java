package edu.iu.informatics.omics.impl;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;

public class AverageMassProxyTest extends TestCase {
	private AverageMassDefinitionImpl averMassDifinition;

	private IMassProxy massProxy;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		massProxy = MassProxyFactory.getMassProxy(IsotopicType.Average, DerivativeType.Underivatised,AdductType.H);
		averMassDifinition = new AverageMassDefinitionImpl();
	}

	public void testGetProton() {
		massProxy.setAdduct(AdductType.H);
		assertEquals(averMassDifinition.getProton(AdductType.H), massProxy
				.getAdduct());
		massProxy.setAdduct(AdductType.K);
		assertEquals(averMassDifinition.getProton(AdductType.K), massProxy
				.getAdduct());
		massProxy.setAdduct(AdductType.Na);
		assertEquals(averMassDifinition.getProton(AdductType.Na), massProxy
				.getAdduct());

	}

	public void testGetH2O() {
		assertEquals(averMassDifinition.getAtom('H') * 2 + averMassDifinition.getAtom('O'),
				massProxy.getH2O());
	}

}
