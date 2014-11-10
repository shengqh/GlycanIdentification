package edu.iu.informatics.omics.impl.fragmentation;

import java.util.List;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.IMonosaccharide;

public class OligosaccharideFragmentationYBuilderTest extends TestCase {
	OligosaccharideFragmentationYBuilder builder = new OligosaccharideFragmentationYBuilder();

	public void testBuild() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(IsotopicType.Average,
				DerivativeType.Underivatised, AdductType.Na);
		Glycan glycan = new Glycan("Temp", "Glc6-1GlcNAc");
		List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();

		assertEquals(203.1474, builder.build(massProxy, oss.get(0)).getMz(), 0.01);
	}

}
