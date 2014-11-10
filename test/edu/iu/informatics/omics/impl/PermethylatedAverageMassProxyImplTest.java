package edu.iu.informatics.omics.impl;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import junit.framework.TestCase;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class PermethylatedAverageMassProxyImplTest extends TestCase {

	public void testModifyGlycan() {
		IGlycanParser parser = new GlycanColFormat();

		IMassProxy mp = MassProxyFactory.getMassProxy(IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, AdductType.Na);
		Glycan glycan5 = new Glycan("Permethylated_Dextran_glc5", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Glycan glycan6 = new Glycan("Permethylated_Dextran_glc6", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Glycan glycan7 = new Glycan("Permethylated_Dextran_glc7", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Glycan glycan8 = new Glycan("Permethylated_Dextran_glc8", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Glycan glycan9 = new Glycan("Permethylated_Dextran_glc9", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Glycan glycan10 = new Glycan("Permethylated_Dextran_glc10", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		mp.modifyGlycan(glycan5);
		mp.modifyGlycan(glycan6);
		mp.modifyGlycan(glycan7);
		mp.modifyGlycan(glycan8);
		mp.modifyGlycan(glycan9);
		mp.modifyGlycan(glycan10);
		assertEquals(1089.53, glycan5.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
		assertEquals(1293.63, glycan6.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
		assertEquals(1497.73, glycan7.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
		assertEquals(1701.83, glycan8.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
		assertEquals(1905.93, glycan9.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
		assertEquals(2110.03, glycan10.getReducingTerm().getMassAll(
				mp.getMassDefinition())
				+ mp.getAdduct(), 0.03);
	}

}
