package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.Pair;
import cn.ac.rcpa.utils.RcpaFileUtils;
import cn.ac.rcpa.utils.RcpaMathUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class CrossRingIonTypeFinder implements IFileProcessor {
	private static final double THRESHOLD = 0.3;

	private static String DEXTRAN = "Dextran_glc";

	private static String MALTOOLIGOSACCHARIDES = "Maltooligosaccharides_glc";

	private static FragmentationType[] removedTypes = new FragmentationType[] {
			FragmentationType.B, FragmentationType.C, FragmentationType.A13,
			FragmentationType.Y, FragmentationType.Z, FragmentationType.X13 };

	private List<FragmentationType> ionTypes;

	private String dextranNamePrefix;

	private String maltoNamePrefix;

	private String[] nameLoops;

	private IMassProxy massProxy;

	private FragmentationBuilder<Peak> builder;

	private PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();
	
	private IGlycanReader glycanReader;

	public CrossRingIonTypeFinder(IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType,
			AdductType aType, String dextranNamePrefix, String maltoNamePrefix,
			String[] nameLoops) {
		super();
		this.glycanReader = glycanReader;
		this.dextranNamePrefix = dextranNamePrefix;
		this.maltoNamePrefix = maltoNamePrefix;
		this.nameLoops = nameLoops;
		this.massProxy = MassProxyFactory.getMassProxy(iType, dType, aType);
		this.builder = new LinearFragmentationBuilderImpl(massProxy);

		ionTypes = new ArrayList<FragmentationType>(Arrays
				.asList(FragmentationType.items));

		for (FragmentationType fType : removedTypes) {
			ionTypes.remove(fType);
		}
		Collections.sort(ionTypes);
	}

	public List<String> process(String dataDir) throws Exception {
		Map<FragmentationType, List<Integer>> dextran16 = calculate(
				dextranNamePrefix, dataDir);

		Map<FragmentationType, List<Integer>> maltooligsaccharides14 = calculate(
				maltoNamePrefix, dataDir);

		String resultFile = new File(dataDir, new File(dataDir).getName()
				+ "_16_14_order.xls").getAbsolutePath();
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			for (FragmentationType type : dextran16.keySet()) {
				pw.print(type);

				final List<Integer> orders16 = dextran16.get(type);
				final List<Integer> orders14 = maltooligsaccharides14.get(type);
				double p = RcpaMathUtils
						.twoTailsProbabilityStudentT(orders16, orders14);
				final int freedom = orders16.size() + orders14.size() - 2;

				pw.println("\t" + freedom + "\t" + p);

				pw.print("16");
				for (Integer order : orders16) {
					pw.print("\t" + order);
				}
				pw.println();

				pw.print("14");
				for (Integer order : maltooligsaccharides14.get(type)) {
					pw.print("\t" + order);
				}
				pw.println();
				pw.println();
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

	private Map<FragmentationType, List<Integer>> calculate(String namePrefix,
			String dataDir) throws Exception {

		List<File> files = new ArrayList<File>();
		for (String loop : nameLoops) {
			files.add(new File(dataDir, namePrefix + loop + ".inp"));
		}
		return detectCrossringOrder(namePrefix, files.toArray(new File[0]));
	}

	private Map<FragmentationType, List<Integer>> detectCrossringOrder(
			String namePrefix, File[] files) throws Exception {

		LinkedHashMap<FragmentationType, List<Integer>> result = new LinkedHashMap<FragmentationType, List<Integer>>();
		for (FragmentationType aType : ionTypes) {
			result.put(aType, new ArrayList<Integer>());
		}

		for (File file : files) {
			final String originPeakFile = RcpaFileUtils.changeExtension(file
					.getAbsolutePath(), "txt.ann");
			final PeakList<Peak> originPeaks = reader.read(originPeakFile);

			final Glycan glycan = glycanReader.read(file.getAbsolutePath());

			if (!glycan.isLinear()) {
				throw new IllegalStateException("Glycan from " + file.getName()
						+ " is not linear :" + glycan);
			}

			String rankResultFile = originPeakFile + ".rank";
			PrintWriter pw = new PrintWriter(rankResultFile);
			try {
				pw.println(glycan.toString());
				
				List<IMonosaccharide> oss = glycan
						.getOligosaccharidesFromNonreducingTerm();

				for (int i = 1; i < oss.size() - 1; i++) {
					Map<FragmentationType, Peak> virtualPeaks = builder.buildMap(oss
							.get(i));
					for (FragmentationType atype : removedTypes) {
						virtualPeaks.remove(atype);
					}

					List<Pair<FragmentationType, Peak>> peaks = convertFromMap(virtualPeaks);
					for (Pair<FragmentationType, Peak> pair : peaks) {
						double intensity = getMaxIntensity(pair.snd, originPeaks);
						pair.snd.setIntensity(intensity);
					}
					Collections.sort(peaks,
							new Comparator<Pair<FragmentationType, Peak>>() {
								public int compare(Pair<FragmentationType, Peak> o1,
										Pair<FragmentationType, Peak> o2) {
									int compareResult = Double.compare(o2.snd.getIntensity(),
											o1.snd.getIntensity());
									if (0 == compareResult) {
										compareResult = o1.fst.getName()
												.compareTo(o2.fst.getName());
									}
									return compareResult;
								}
							});

				
					pw.println("-------" + i + "-------");
					
					System.out.println(peaks);
					System.out.println("-------------");

					int lastOrder = 1;
					Peak lastPeak = peaks.get(0).snd;
					for (int order = 0; order < peaks.size(); order++) {
						final Pair<FragmentationType, Peak> pair = peaks.get(order);
						if (0 == order) {
							result.get(pair.fst).add(1);
						} else if (pair.snd.getIntensity() == lastPeak.getIntensity()) {
							result.get(pair.fst).add(lastOrder);
						} else {
							lastOrder = order + 1;
							lastPeak = pair.snd;
							result.get(pair.fst).add(lastOrder);
						}
						pw.println(pair.snd.toString()+ "\t" + lastOrder);
					}
				}
			} finally {
				pw.close();
			}
		}

		return result;
	}

	private static List<Pair<FragmentationType, Peak>> convertFromMap(
			Map<FragmentationType, Peak> virtualPeaks) {
		ArrayList<Pair<FragmentationType, Peak>> result = new ArrayList<Pair<FragmentationType, Peak>>();
		for (FragmentationType key : virtualPeaks.keySet()) {
			result.add(new Pair<FragmentationType, Peak>(key, virtualPeaks.get(key)));
		}
		return result;
	}

	private static double getMaxIntensity(Peak peak, PeakList<Peak> originPeaks) {
		double result = 0.0;
		for (Peak originPeak : originPeaks.getPeaks()) {
			if (Math.abs(peak.getMz() - originPeak.getMz()) < THRESHOLD) {
				if (result < originPeak.getIntensity()) {
					result = originPeak.getIntensity();
				}
			}
		}

		return result;
	}
	
	public static void main(String[] args) throws Exception {
		new CrossRingIonTypeFinder(new GlycanColFormat(), IsotopicType.Average,
				DerivativeType.Underivatised, AdductType.Na, DEXTRAN,
				MALTOOLIGOSACCHARIDES, new String[] { "05", "06", "07", "08", "09",
						"10" }).process("data/Underivatised");

		new CrossRingIonTypeFinder(new GlycanColFormat(), IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, AdductType.Na,
				"Permethylated_" + DEXTRAN, "Permethylated_" + MALTOOLIGOSACCHARIDES,
				new String[] { "06", "07", "08", "09", "10" })
				.process("data/NewPermethylated");
	}
}
