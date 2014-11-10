package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.Pair;
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
 * 计算各种糖链中，去除首尾两个oligosaccharide，中间的oligosaccharide对应各种离子的出现概率。
 * 
 * @author sheng
 * 
 */
public class LinearGlycanFragmentationMatchCountCalculator {
	private static final double THRESHOLD = 0.3;

	public static void main(String[] args) throws Exception {
		File[] files = new File("data/Underivatised")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith("inp");
					}
				});

		IGlycanReader glycanReader = new GlycanColFormat();
		IMassProxy averMassProxy = MassProxyFactory.getMassProxy(IsotopicType.Average,
				DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> averBuilder = new LinearFragmentationBuilderImpl(
				averMassProxy);

		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();

		detect(files, averBuilder, "average", reader,glycanReader);

		// MassProxy monoMassProxy = MassProxyFactory.getMassProxy(true,
		// DerivativeType.Underivatised, ProtonType.Na);
		//
		// GlycanFragmentationBuilder<Peak> monoBuilder = new
		// GlycanFragmentationBuilderImpl(
		// monoMassProxy);
		//		
		// detect(files, monoBuilder, "mono", reader,glycanReader);
	}

	private static void detect(File[] files, FragmentationBuilder<Peak> builder,
			String isotopic, PeakListReader<Peak> reader ,IGlycanReader glycanReader) throws Exception,
			FileNotFoundException {
		final Map<String, List<String>> glycanFileMap = getGlycanFileMap(files);
		final Map<FragmentationType, Map<String, Pair<Integer, Integer>>> result = new HashMap<FragmentationType, Map<String, Pair<Integer, Integer>>>();

		final DecimalFormat df = new DecimalFormat("0.00");
		// System.out.println(glycanFileMap);
		for (String glycanStr : glycanFileMap.keySet()) {
			System.out.println(glycanStr);

			final List<String> filenames = glycanFileMap.get(glycanStr);
			for (String filename : filenames) {
				// System.out.println(filename);

				Glycan glycan = glycanReader.read(filename);
				Map<FragmentationType, List<Peak>> virtualPeaks = builder
						.buildMapWithoutTerminalOligosaccharide(glycan);

				virtualPeaks.remove(FragmentationType.C);
				virtualPeaks.remove(FragmentationType.Z);

				String originPeakFile = RcpaFileUtils.changeExtension(filename,
						"txt.ann");
				PeakList<Peak> originPeaks = reader.read(originPeakFile);

				for (FragmentationType type : virtualPeaks.keySet()) {
					final List<Peak> peaks = virtualPeaks.get(type);
					int matchCount = getMatchCount(peaks, originPeaks);
					if (!result.containsKey(type)) {
						result.put(type, new HashMap<String, Pair<Integer, Integer>>());
					}
					Map<String, Pair<Integer, Integer>> ionAnalysis = result.get(type);

					if (!ionAnalysis.containsKey(glycanStr)) {
						ionAnalysis.put(glycanStr, new Pair<Integer, Integer>(0, 0));
					}
					final Pair<Integer, Integer> oldPair = ionAnalysis.get(glycanStr);
					ionAnalysis.put(glycanStr, new Pair<Integer, Integer>(oldPair.fst
							+ matchCount, oldPair.snd + peaks.size()));
				}
			}
		}
		// System.out.println(result);
		final File resultPeakFile = new File(files[0].getParentFile(), isotopic
				+ "_matchcount_" + THRESHOLD + ".xls");
		PrintWriter pw = new PrintWriter(resultPeakFile);
		try {
			List<String> glycanStrs = new ArrayList<String>(result.entrySet()
					.iterator().next().getValue().keySet());
			Collections.sort(glycanStrs);

			for (String glycanStr : glycanStrs) {
				pw.print("\t" + glycanStr + "\t\t");
			}
			pw.println();

			for (FragmentationType type : FragmentationType.items) {
				if (!result.containsKey(type)) {
					continue;
				}
				Map<String, Pair<Integer, Integer>> ionAnalysis = result.get(type);
				pw.print(type);

				for (String glcanStr : glycanStrs) {
					final Pair<Integer, Integer> countPair = ionAnalysis.get(glcanStr);
					pw.print("\t"
							+ countPair.fst
							+ "\t"
							+ countPair.snd
							+ "\t"
							+ (countPair.snd == 0 ? 0 : df
									.format((double) (countPair.fst * 100) / countPair.snd)));
				}
				pw.println();
			}
		} finally {
			pw.close();
		}

	}

	private static int getMatchCount(List<Peak> peaks, PeakList<Peak> originPeaks) {
		int result = 0;
		for (Peak peak : peaks) {
			for (Peak originPeak : originPeaks.getPeaks()) {
				if (Math.abs(peak.getMz() - originPeak.getMz()) < THRESHOLD) {
					result++;
					break;
				}
			}
		}
		return result;
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
