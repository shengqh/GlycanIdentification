package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.RcpaMathUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.GlycanUtils;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

/**
 * Find the cross ring ion type with significant different order between 1,6
 * linkage and 1,4 linkage
 * 
 * @author sheng
 * 
 */
public class CrossRingIonTypeDifferentOrderFinder implements IFileProcessor {
	private double peakThreshold;

	private static String DEXTRAN = "Dextran_glc";

	private static String MALTOOLIGOSACCHARIDES = "Maltooligosaccharides_glc";

	private static List<FragmentationType> removedTypes = Arrays
			.asList(new FragmentationType[] { FragmentationType.B,
					FragmentationType.C, FragmentationType.A13, FragmentationType.Y,
					FragmentationType.Z, FragmentationType.X13 });

	private String dextranNamePrefix;

	private String maltoNamePrefix;

	private String[] nameLoops;

	private IMassProxy massProxy;

	private FragmentationBuilder<Peak> builder;

	private PeakListReader<Peak> pklReader = new MaldiPeakListReaderImpl();

	private IGlycanReader glycanReader;

	public CrossRingIonTypeDifferentOrderFinder(IGlycanReader glycanReader,
			PeakListReader<Peak> pklReader, IsotopicType iType, DerivativeType dType,
			AdductType aType, String dextranNamePrefix, String maltoNamePrefix,
			String[] nameLoops, double peakThreshold) {
		super();
		this.glycanReader = glycanReader;
		this.pklReader = pklReader;
		this.dextranNamePrefix = dextranNamePrefix;
		this.maltoNamePrefix = maltoNamePrefix;
		this.nameLoops = nameLoops;
		this.massProxy = MassProxyFactory.getMassProxy(iType, dType, aType);
		this.builder = new LinearFragmentationBuilderImpl(massProxy);
		this.peakThreshold = peakThreshold;
	}

	public List<String> process(String dataDir) throws Exception {
		Map<FragmentationType, List<Integer>> dextran16 = getFragmentationTypeOrderListMap(
				dextranNamePrefix, dataDir, 6);

		Map<FragmentationType, List<Integer>> dextran14 = getFragmentationTypeOrderListMap(
				dextranNamePrefix, dataDir, 4);

		Map<FragmentationType, List<Integer>> maltoo14 = getFragmentationTypeOrderListMap(
				maltoNamePrefix, dataDir, 4);

		Map<FragmentationType, List<Integer>> maltoo16 = getFragmentationTypeOrderListMap(
				maltoNamePrefix, dataDir, 6);

		String result16File = new File(dataDir, new File(dataDir).getName()
				+ "_16_order.xls").getAbsolutePath();

		detectDifference(result16File, dextran16, maltoo16);

		String result14File = new File(dataDir, new File(dataDir).getName()
				+ "_14_order.xls").getAbsolutePath();

		detectDifference(result14File, maltoo14, dextran14);

		return Arrays.asList(new String[] { result16File, result14File });
	}

	private String detectDifference(String resultFile,
			Map<FragmentationType, List<Integer>> real,
			Map<FragmentationType, List<Integer>> fake) throws FileNotFoundException {
		DecimalFormat df2 = new DecimalFormat("0.00");

		PrintWriter pw = new PrintWriter(resultFile);
		try {
			for (FragmentationType type : real.keySet()) {
				pw.print(type);

				final List<Integer> realOrder = real.get(type);
				final List<Integer> fakeOrder = fake.get(type);

				System.out.println("FragmentationType=" + type);
				System.out.println("Real=" + realOrder);
				System.out.println("Fake=" + fakeOrder);
				double p = RcpaMathUtils.twoTailsProbabilityStudentT(realOrder,
						fakeOrder);
				final int freedom = realOrder.size() + fakeOrder.size() - 2;

				pw.println("\t" + freedom + "\t" + p);

				DoubleArrayList realOrderList = RcpaMathUtils
						.getDoubleArrayList(realOrder);
				final double meanReal = Descriptive.mean(RcpaMathUtils
						.getDoubleArrayList(realOrder));
				final double svReal = Descriptive.sampleVariance(realOrderList,
						meanReal);

				DoubleArrayList fakeOrderList = RcpaMathUtils
						.getDoubleArrayList(realOrder);
				final double meanFake = Descriptive.mean(RcpaMathUtils
						.getDoubleArrayList(fakeOrder));
				final double svFake = Descriptive.sampleVariance(fakeOrderList,
						meanFake);

				pw.print("Real\t" + df2.format(meanReal) + "\t" + df2.format(svReal));
				for (Integer order : realOrder) {
					pw.print("\t" + order);
				}
				pw.println();

				pw.print("Fake\t" + df2.format(meanFake) + "\t" + df2.format(svFake));
				for (Integer order : fakeOrder) {
					pw.print("\t" + order);
				}
				pw.println();
				pw.println();
			}
		} finally {
			pw.close();
		}
		return resultFile;
	}

	private Map<FragmentationType, List<Integer>> getFragmentationTypeOrderListMap(
			String namePrefix, String dataDir, int assumptConnectionType)
			throws Exception {

		List<File> files = new ArrayList<File>();
		for (String loop : nameLoops) {
			files.add(new File(dataDir, namePrefix + loop + ".inp"));
		}
		return getFragmentationTypeOrderListMap(namePrefix, files
				.toArray(new File[0]), assumptConnectionType);
	}

	private Map<FragmentationType, List<Integer>> getFragmentationTypeOrderListMap(
			String namePrefix, File[] files, int assumptConnectionType)
			throws Exception {

		LinkedHashMap<FragmentationType, List<Integer>> result = new LinkedHashMap<FragmentationType, List<Integer>>();
		for (FragmentationType fType : FragmentationType.items) {
			result.put(fType, new ArrayList<Integer>());
		}

		for (File file : files) {
			final String originPeakFile = RcpaFileUtils.changeExtension(file
					.getAbsolutePath(), "txt.ann");
			final PeakList<Peak> originPeaks = pklReader.read(originPeakFile);

			final Glycan glycan = GlycanUtils.readAs(glycanReader, file
					.getAbsolutePath(), assumptConnectionType);

			if (!glycan.isLinear()) {
				throw new IllegalStateException("Glycan from " + file.getName()
						+ " is not linear :" + glycan);
			}

			List<IMonosaccharide> oss = glycan
					.getOligosaccharidesFromNonreducingTerm();

			System.out.println(file.getName() + " as 1," + assumptConnectionType);
			//for (int i = 1; i < oss.size() - 1; i++) {
			for (int i = 2; i < oss.size() - 1; i++) {
				Map<FragmentationType, Peak> virtualPeaks = builder
						.buildMap(oss.get(i));

				System.out.println("\tposition" + (i+1));
				Map<FragmentationType, Integer> map = GlycanUtils.getOrderMap(
						removedTypes, null, virtualPeaks, originPeaks, peakThreshold);

				for (FragmentationType fType : map.keySet()) {
					result.get(fType).add(map.get(fType));
				}
			}
		}

		for (FragmentationType fType : FragmentationType.items) {
			if (0 == result.get(fType).size()) {
				result.remove(fType);
			}
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		 maldiDataAnalysis();
		//pdDataAnalysis();
	}

	protected static void maldiDataAnalysis() throws Exception {
		GlycanColFormat gcf = new GlycanColFormat();
		MaldiPeakListReaderImpl pklReader = new MaldiPeakListReaderImpl();
//		new CrossRingIonTypeDifferentOrderFinder(gcf, pklReader,
//				IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na,
//				DEXTRAN, MALTOOLIGOSACCHARIDES, new String[] { "05", "06", "07", "08",
//						"09", "10" }, 0.3).process(DataLocation.maldiUnderivatisedDir);

		new CrossRingIonTypeDifferentOrderFinder(gcf, pklReader,
				IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na,
				"Permethylated_" + DEXTRAN, "Permethylated_" + MALTOOLIGOSACCHARIDES,
				new String[] { "06", "07", "08", "09", "10" }, 0.3)
				.process(DataLocation.maldiPermethylatedDir);
	}

	protected static void pdDataAnalysis() throws Exception {
		GlycanColFormat gcf = new GlycanColFormat();
		MaldiPeakListReaderImpl pklReader = new MaldiPeakListReaderImpl();
		// MascotGenericFormatReader pklReader = new MascotGenericFormatReader();
		// new CrossRingIonTypeDifferentOrderFinder(gcf, pklReader,
		// IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na,
		// DEXTRAN, MALTOOLIGOSACCHARIDES, new String[] { "05", "06", "07", "08",
		// "09", "10" }).process(DataLocation.pdUnderivatisedDir);

		new CrossRingIonTypeDifferentOrderFinder(gcf, pklReader,
				IsotopicType.Average, DerivativeType.Permethylated, AdductType.Na,
				"Permethylated_" + DEXTRAN, "Permethylated_" + MALTOOLIGOSACCHARIDES,
				new String[] { "05", "06", "07", "08", "09", "10" }, 0.8)
				.process(DataLocation.pdPermethylatedDir);
	}
}
