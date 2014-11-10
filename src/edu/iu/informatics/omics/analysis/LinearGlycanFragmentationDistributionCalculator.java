package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

/**
 * 这个程序用来检测不同类型糖链，实际离子跟理论的b/y系列之间的差距。 试图找出哪些距离（对应离子）出现的概率。类似Dancik方法，但感觉不奏效。
 * 
 * @author sheng
 * 
 */
public class LinearGlycanFragmentationDistributionCalculator {
	private static final double MZRANGE = 20;

	private static final double THRESHOLD = 0.2;

	public static void main(String[] args) throws Exception {
		File[] files = new File("data/Underivatised")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith("inp");
					}
				});

		IGlycanReader glycanReader = new GlycanColFormat();

		IMassProxy averMassProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> averBuilder = new LinearFragmentationBuilderImpl(
				averMassProxy);

		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();

		detect(files, averBuilder, "average", reader, glycanReader);

		IMassProxy monoMassProxy = MassProxyFactory.getMassProxy(
				IsotopicType.Monoisotopic, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> monoBuilder = new LinearFragmentationBuilderImpl(
				monoMassProxy);

		detect(files, monoBuilder, "mono", reader, glycanReader);

	}

	private static void detect(File[] files, FragmentationBuilder<Peak> builder,
			String isotopic, PeakListReader<Peak> reader, IGlycanReader glycanReader)
			throws Exception, FileNotFoundException {
		final Map<String, List<String>> glycanFileMap = getGlycanFileMap(files);

		final DecimalFormat df = new DecimalFormat("0.00");

		final int range = (int) (MZRANGE / THRESHOLD);
		final double scale = 1.0 / THRESHOLD;

		HashMap<String, int[]> countMap = new HashMap<String, int[]>();
		// System.out.println(glycanFileMap);
		for (String glycanStr : glycanFileMap.keySet()) {
			System.out.println(glycanStr);

			final int[] counts = new int[range * 2];
			for (int i = 0; i < counts.length; i++) {
				counts[i] = 0;
			}

			countMap.put(glycanStr, counts);

			final List<String> filenames = glycanFileMap.get(glycanStr);
			for (String filename : filenames) {
				// System.out.println(filename);

				Glycan glycan = glycanReader.read(filename);
				Map<FragmentationType, List<Peak>> virtualPeaks = builder
						.buildMap(glycan);

				String originPeakFile = RcpaFileUtils.changeExtension(filename,
						"txt.ann");
				PeakList<Peak> originPeaks = reader.read(originPeakFile);

				List<Peak> bSeries = virtualPeaks.get(FragmentationType.B);
				for (Peak bPeak : bSeries) {
					for (Peak originPeak : originPeaks.getPeaks()) {
						int gap = (int) ((originPeak.getMz() - bPeak.getMz()) * scale)
								+ range;
						if (gap < 0) {
							continue;
						}
						if (gap >= counts.length) {
							break;
						}
						counts[gap]++;
					}
				}
			}
			final File resultPeakFile = new File(files[0].getParentFile(), glycanStr
					+ "." + isotopic + ".gap.xls");
			PrintWriter pw = new PrintWriter(resultPeakFile);
			try {
				for (int i = 0; i < counts.length; i++) {
					pw.println(df.format((i - range) * THRESHOLD) + "\t" + counts[i]);
				}
			} finally {
				pw.close();
			}
		}

		final File resultPeakFile = new File(files[0].getParentFile(), isotopic
				+ ".gap.xls");
		PrintWriter pw = new PrintWriter(resultPeakFile);
		try {
			for (String glycanStr : countMap.keySet()) {
				pw.print("\t" + glycanStr);
			}
			pw.println();

			int countLength = countMap.entrySet().iterator().next().getValue().length;

			for (int i = 0; i < countLength; i++) {
				pw.print(df.format((i - range) * THRESHOLD));
				for (String glycanStr : countMap.keySet()) {
					pw.print("\t" + countMap.get(glycanStr)[i]);
				}
				pw.println();
			}
		} finally {
			pw.close();
		}

		// System.out.println(result);
		// final File resultPeakFile = new File(files[0].getParentFile(), isotopic
		// + ".analysis.xls");
		// PrintWriter pw = new PrintWriter(resultPeakFile);
		// try {
		// List<String> types = new ArrayList<String>(result.keySet());
		// Collections.sort(types);
		//
		// List<String> glycanStrs = new ArrayList<String>(result.get(types.get(0))
		// .keySet());
		// Collections.sort(glycanStrs);
		//
		// for (String glycanStr : glycanStrs) {
		// pw.print("\t" + glycanStr + "\t\t");
		// }
		// pw.println();
		//
		// for (String type : types) {
		// Map<String, Pair<Integer, Integer>> ionAnalysis = result.get(type);
		// pw.print(type);
		//
		// for (String glcanStr : glycanStrs) {
		// final Pair<Integer, Integer> countPair = ionAnalysis.get(glcanStr);
		// pw.print("\t"
		// + countPair.fst
		// + "\t"
		// + countPair.snd
		// + "\t"
		// + (countPair.snd == 0 ? 0 : df
		// .format((double) (countPair.fst * 100) / countPair.snd)));
		// }
		// pw.println();
		// }
		// } finally {
		// pw.close();
		// }

	}

	private static Map<String, List<String>> getGlycanFileMap(File[] files) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		Pattern filePattern = Pattern.compile("(.*)_glc");
		for (File file : files) {
			Matcher matcher = filePattern.matcher(file.getName());
			matcher.find();
			if (!result.containsKey(matcher.group(1))) {
				result.put(matcher.group(1), new ArrayList<String>());
			}

			result.get(matcher.group(1)).add(file.getAbsolutePath());
		}
		return result;
	}
}
