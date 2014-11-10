package edu.iu.informatics.omics.impl;

import java.util.Map;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class BranchFragmentationBuilderImplTest extends TestCase {
	private static final double PEAK_THRESHOLD = 0.4;

	private IGlycanParser parser = new GlycanColFormat();

	/**
	 * Figure 1,Yehia, Anal. Chem. 2003,75,4895-4903
	 * 
	 */
	public void testBuildMapLinearGlycan16() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser,
				"Glc6-1Glc6-1Glc6-1Glc6-1Glc6-1Glc");
		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		assertEquals(231.06520, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);
		assertEquals(245.0847, peakMap.get("0,4A2").getMz(), PEAK_THRESHOLD);
		assertEquals(259.0930, peakMap.get("3,5A2").getMz(), PEAK_THRESHOLD);
		assertEquals(275.0480, peakMap.get("0,3A2").getMz(), PEAK_THRESHOLD);
		assertEquals(305.0989, peakMap.get("0,2A2").getMz(), PEAK_THRESHOLD);
		assertEquals(347.1051, peakMap.get("B2").getMz(), PEAK_THRESHOLD);
		assertEquals(365.1093, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);
		assertEquals(393.1043, peakMap.get("1,5X2").getMz(), PEAK_THRESHOLD);
		assertEquals(407.1167, peakMap.get("0,4A3").getMz(), PEAK_THRESHOLD);
		// assertEquals(419.1264, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);
		assertEquals(437.1085, peakMap.get("0,3A3").getMz(), PEAK_THRESHOLD);
		assertEquals(467.1307, peakMap.get("0,2A3").getMz(), PEAK_THRESHOLD);
		assertEquals(509.1312, peakMap.get("B3").getMz(), PEAK_THRESHOLD);
		// Error peak of Y3 in figure 1 which marked as 537.1093
		assertEquals(527.1093, peakMap.get("Y3").getMz(), PEAK_THRESHOLD);
		assertEquals(555.1434, peakMap.get("1,5X3").getMz(), PEAK_THRESHOLD);
		assertEquals(569.1440, peakMap.get("0,4A4").getMz(), PEAK_THRESHOLD);
		assertEquals(583.1622, peakMap.get("3,5A4").getMz(), PEAK_THRESHOLD);
		assertEquals(599.1337, peakMap.get("0,3A4").getMz(), PEAK_THRESHOLD);
		assertEquals(629.1758, peakMap.get("0,2A4").getMz(), PEAK_THRESHOLD);
		assertEquals(671.1621, peakMap.get("B4").getMz(), PEAK_THRESHOLD);
		assertEquals(689.1794, peakMap.get("Y4").getMz(), PEAK_THRESHOLD);
		assertEquals(717.1877, peakMap.get("1,5X4").getMz(), PEAK_THRESHOLD);
		assertEquals(731.1984, peakMap.get("0,4A5").getMz(), PEAK_THRESHOLD);
		assertEquals(745.2341, peakMap.get("3,5A5").getMz(), PEAK_THRESHOLD);
		assertEquals(761.2038, peakMap.get("0,3A5").getMz(), PEAK_THRESHOLD);
		assertEquals(791.2245, peakMap.get("0,2A5").getMz(), PEAK_THRESHOLD);
		assertEquals(833.2390, peakMap.get("B5").getMz(), PEAK_THRESHOLD);
		assertEquals(851.2573, peakMap.get("Y5").getMz(), PEAK_THRESHOLD);
		assertEquals(879.2690, peakMap.get("1,5X5").getMz(), PEAK_THRESHOLD);
		assertEquals(893.2977, peakMap.get("0,4A6").getMz(), PEAK_THRESHOLD);
		assertEquals(907.3234, peakMap.get("3,5A6").getMz(), PEAK_THRESHOLD);
		assertEquals(923.2974, peakMap.get("0,3A6").getMz(), PEAK_THRESHOLD);
		assertEquals(953.3155, peakMap.get("0,2A6").getMz(), PEAK_THRESHOLD);
	}

	/**
	 * Figure 2,Yehia, Anal. Chem. 2003,75,4895-4903
	 * 
	 */
	public void testBuildMapLinearGlycan14() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser,
				"Glc4-1Glc4-1Glc4-1Glc4-1Glc4-1Glc");
		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		assertEquals(185.0337, peakMap.get("B1").getMz(), PEAK_THRESHOLD);
		assertEquals(203.0501, peakMap.get("Y1").getMz(), PEAK_THRESHOLD);
		assertEquals(231.0354, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);
		assertEquals(245.1041, peakMap.get("2,4A2").getMz(), PEAK_THRESHOLD);
		assertEquals(259.0706, peakMap.get("3,5A2").getMz(), PEAK_THRESHOLD);
		assertEquals(347.0911, peakMap.get("B2").getMz(), PEAK_THRESHOLD);
		assertEquals(365.1079, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);
		assertEquals(393.1258, peakMap.get("1,5X2").getMz(), PEAK_THRESHOLD);
		assertEquals(407.1332, peakMap.get("2,4A3").getMz(), PEAK_THRESHOLD);
		assertEquals(421.1675, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);
		assertEquals(509.1457, peakMap.get("B3").getMz(), PEAK_THRESHOLD);
		assertEquals(527.1598, peakMap.get("Y3").getMz(), PEAK_THRESHOLD);
		assertEquals(555.1890, peakMap.get("1,5X3").getMz(), PEAK_THRESHOLD);
		assertEquals(569.2050, peakMap.get("2,4A4").getMz(), PEAK_THRESHOLD);
		assertEquals(583.2302, peakMap.get("3,5A4").getMz(), PEAK_THRESHOLD);
		assertEquals(671.1998, peakMap.get("B4").getMz(), PEAK_THRESHOLD);
		assertEquals(689.2091, peakMap.get("Y4").getMz(), PEAK_THRESHOLD);
		assertEquals(717.2617, peakMap.get("1,5X4").getMz(), PEAK_THRESHOLD);
		assertEquals(731.2784, peakMap.get("2,4A5").getMz(), PEAK_THRESHOLD);
		assertEquals(745.3187, peakMap.get("3,5A5").getMz(), PEAK_THRESHOLD);
		assertEquals(833.2594, peakMap.get("B5").getMz(), PEAK_THRESHOLD);
		assertEquals(851.2678, peakMap.get("Y5").getMz(), PEAK_THRESHOLD);
		assertEquals(879.3147, peakMap.get("1,5X5").getMz(), PEAK_THRESHOLD);
		assertEquals(953.3680, peakMap.get("0,2A6").getMz(), PEAK_THRESHOLD);
		assertEquals(953.3680, peakMap.get("0,4X5").getMz(), PEAK_THRESHOLD);
		// assertEquals(953.3680, peakMap.get("3,5A6").getMz(), PEAK_THRESHOLD);
	}

	/*
	 * Test by Yehia, Rapid Commun. Mass Spectrom. 2006;20:1381-1389, Figure 1
	 */
	public void testLinearPermethylated1() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser, "Glc4-1Glc3-2Neu5Ac");

		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		assertEquals(111.1, peakMap.get("0,2X0").getMz(), PEAK_THRESHOLD);
		assertEquals(127.1, peakMap.get("2,5X0").getMz(), PEAK_THRESHOLD);
		assertEquals(155.1, peakMap.get("0,3X0").getMz(), PEAK_THRESHOLD);
		assertEquals(254.1, peakMap.get("3,5A1").getMz(), PEAK_THRESHOLD);
		assertEquals(287.1, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);
		assertEquals(463.2, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);
		assertEquals(549.3, peakMap.get("1,5X2").getMz(), PEAK_THRESHOLD);
		assertEquals(690.5, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);
	}

	/*
	 * Test by Yehia, Rapid Commun. Mass Spectrom. 2006;20:1381-1389, Figure 2
	 */
	public void testLinearPermethylated2() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser, "Glc4-1Glc6-2Neu5Ac");

		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		assertEquals(111.1, peakMap.get("0,2X0").getMz(), PEAK_THRESHOLD);
		assertEquals(127.1, peakMap.get("2,5X0").getMz(), PEAK_THRESHOLD);
		assertEquals(155.1, peakMap.get("0,3X0").getMz(), PEAK_THRESHOLD);
		assertEquals(254.1, peakMap.get("3,5A1").getMz(), PEAK_THRESHOLD);
		assertEquals(287.1, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);
		assertEquals(416.2, peakMap.get("C1").getMz(), PEAK_THRESHOLD);
		assertEquals(458.2, peakMap.get("0,4A2").getMz(), PEAK_THRESHOLD);
		assertEquals(463.3, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);
		assertEquals(486.3, peakMap.get("3,5A2").getMz(), PEAK_THRESHOLD);
		assertEquals(547.3, peakMap.get("0,2X2").getMz(), PEAK_THRESHOLD);
		assertEquals(690.5, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);
	}

	/*
	 * Test by Yehia, Rapid Commun. Mass Spectrom. 2006;20:1381-1389, Figure 2
	 */
	public void testLinearPermethylated3() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser,
				"Glc4-1Glc3-1GlcNAc4-1Glc6-2Neu5Ac");

		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		assertEquals(111.1, peakMap.get("0,2X0").getMz(), PEAK_THRESHOLD);
		assertEquals(127.1, peakMap.get("2,5X0").getMz(), PEAK_THRESHOLD);
		assertEquals(155.1, peakMap.get("0,3X0").getMz(), PEAK_THRESHOLD);
		assertEquals(254.1, peakMap.get("3,5A1").getMz(), PEAK_THRESHOLD);
		assertEquals(287.1, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);
		assertEquals(315.1, peakMap.get("0,2X1").getMz(), PEAK_THRESHOLD);
		assertEquals(416.1, peakMap.get("C1").getMz(), PEAK_THRESHOLD);
		assertEquals(458.1, peakMap.get("0,4A2").getMz(), PEAK_THRESHOLD);
		assertEquals(463.2, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);
		assertEquals(486.2, peakMap.get("3,5A2").getMz(), PEAK_THRESHOLD);
		assertEquals(491.2, peakMap.get("1,5X2").getMz(), PEAK_THRESHOLD);
		assertEquals(620.3, peakMap.get("3,5X2").getMz(), PEAK_THRESHOLD);
		assertEquals(690.3, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);

		assertEquals(736.3, peakMap.get("1,5X3").getMz(), PEAK_THRESHOLD);
		assertEquals(819.4, peakMap.get("1,5A3").getMz(), PEAK_THRESHOLD);
		// assertEquals(864.5, peakMap.get("C3").getMz(), PEAK_THRESHOLD);
		assertEquals(912.6, peakMap.get("Y4").getMz(), PEAK_THRESHOLD);
		assertEquals(998.7, peakMap.get("1,5X4").getMz(), PEAK_THRESHOLD);
		assertEquals(1139.9, peakMap.get("3,5A5").getMz(), PEAK_THRESHOLD);
	}

	/**
	 * Figure 5,Yehia, Anal. Chem. 2003,75,4895-4903
	 * 
	 */
	public void testBranchMan5() {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> builder = new BranchFragmentationBuilderImpl(
				massProxy);

		Glycan glycan = new Glycan("Temp", parser,
				"GlcNAc4-1GlcNAc4-1Man(3-1Man,6-1Man(3-1Man,6-1Man))");

		Map<String, Peak> peakMap = builder.buildPeakMap(glycan);

		// System.out.println(builder.build(glycan));

		assertEquals(185.0563, peakMap.get("B1a'").getMz(), PEAK_THRESHOLD);
		assertEquals(185.0563, peakMap.get("B1a\"").getMz(), PEAK_THRESHOLD);
		assertEquals(185.0563, peakMap.get("B2b").getMz(), PEAK_THRESHOLD);

		assertEquals(203.0858, peakMap.get("C1a'").getMz(), PEAK_THRESHOLD);
		assertEquals(203.0858, peakMap.get("C1a\"").getMz(), PEAK_THRESHOLD);
		assertEquals(203.0858, peakMap.get("C2b").getMz(), PEAK_THRESHOLD);

		assertEquals(226.0908, peakMap.get("Z1").getMz(), PEAK_THRESHOLD);

		assertEquals(244.1070, peakMap.get("Y1").getMz(), PEAK_THRESHOLD);

		assertEquals(272.1050, peakMap.get("1,5X1").getMz(), PEAK_THRESHOLD);

		assertEquals(429.1849, peakMap.get("Z2").getMz(), PEAK_THRESHOLD);

		assertEquals(447.1826, peakMap.get("Y2").getMz(), PEAK_THRESHOLD);

		assertEquals(475.1992, peakMap.get("1,5X2").getMz(), PEAK_THRESHOLD);

		assertEquals(509.1753, peakMap.get("B2a").getMz(), PEAK_THRESHOLD);

		assertEquals(527.2029, peakMap.get("C2a").getMz(), PEAK_THRESHOLD);

		assertEquals(583.2186, peakMap.get("3,5A3").getMz(), PEAK_THRESHOLD);

		assertEquals(771.2791, peakMap.get("Y3a").getMz(), PEAK_THRESHOLD);

		assertEquals(799.2855, peakMap.get("1,5X3a").getMz(), PEAK_THRESHOLD);

		assertEquals(907.3265, peakMap.get("3,5A4").getMz(), PEAK_THRESHOLD);

		assertEquals(953.3788, peakMap.get("0,2A4").getMz(), PEAK_THRESHOLD);

		assertEquals(1036.3893, peakMap.get("B4").getMz(), PEAK_THRESHOLD);

		assertEquals(1123.4529, peakMap.get("1,5X4a'").getMz(), PEAK_THRESHOLD);
		assertEquals(1123.4529, peakMap.get("1,5X4a\"").getMz(), PEAK_THRESHOLD);
		assertEquals(1123.4529, peakMap.get("1,5X3b").getMz(), PEAK_THRESHOLD);

		assertEquals(1156.4673, peakMap.get("0,2A5").getMz(), PEAK_THRESHOLD);
		assertEquals(1156.4673, peakMap.get("1,3X0").getMz(), PEAK_THRESHOLD);
	}
}
