package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class LinearGlycanFragmentationIntensityCalculator {
	private static final double THRESHOLD = 0.3;

	public static void main(String[] args) throws Exception {
		File[] files = new File("data/Underivatised").listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith("inp");
			}
		});
		IGlycanReader glycanReader = new GlycanColFormat();

		IMassProxy averMassProxy = MassProxyFactory.getMassProxy(IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na);

		FragmentationBuilder<Peak> averBuilder = new LinearFragmentationBuilderImpl(averMassProxy);

		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();

		detect(files, averBuilder, "average", reader, glycanReader);

		// MassProxy monoMassProxy = MassProxyFactory.getMassProxy(true,
		// DerivativeType.Underivatised, ProtonType.Na);
		//
		// GlycanFragmentationBuilder<Peak> monoBuilder = new
		// GlycanFragmentationBuilderImpl(
		// monoMassProxy);
		//
		// detect(files, monoBuilder, "mono", reader,glycanReader);
	}

	private static void detect(File[] files, FragmentationBuilder<Peak> builder, String isotopic, PeakListReader<Peak> reader, IGlycanReader glycanReader) throws Exception, FileNotFoundException {
		final Map<String, List<String>> glycanFileMap = getGlycanFileMap(files);

		final DecimalFormat df = new DecimalFormat("0.00");

		final File resultTotalIntensityFile = new File(files[0].getParentFile(), isotopic + "_intensity_" + THRESHOLD + "_div_totalIntensity.xls");
		PrintWriter pwTotalIntensity = new PrintWriter(resultTotalIntensityFile);
		final File resultBSeriesFile = new File(files[0].getParentFile(), isotopic + "_intensity_" + THRESHOLD + "_div_BSeries.xls");
		PrintWriter pwBSeries = new PrintWriter(resultBSeriesFile);
		try {
			for (String glycanStr : glycanFileMap.keySet()) {
				System.out.println(glycanStr);

				final List<String> filenames = glycanFileMap.get(glycanStr);
				final Map<String, Map<FragmentationType, Double>> percentMap = new HashMap<String, Map<FragmentationType, Double>>();
				for (String filename : filenames) {
					Glycan glycan = glycanReader.read(filename);
					Map<FragmentationType, List<Peak>> virtualPeaks = builder.buildMap(glycan);

					String originPeakFile = RcpaFileUtils.changeExtension(filename, "txt.ann");
					PeakList<Peak> originPeaks = reader.read(originPeakFile);
					final double totalOriginIntensity = originPeaks.getTotalIntensity();

					Map<FragmentationType, Double> typePercent = new LinkedHashMap<FragmentationType, Double>();
					percentMap.put(new File(filename).getName(), typePercent);
					for (FragmentationType type : virtualPeaks.keySet()) {
						List<Peak> peaks = virtualPeaks.get(type);
						double totalIntensity = 0.0;

						for (Peak peak : peaks) {
							for (Peak originPeak : originPeaks.getPeaks()) {
								if (Math.abs(peak.getMz() - originPeak.getMz()) < THRESHOLD) {
									totalIntensity += originPeak.getIntensity();
								}
							}
						}
						typePercent.put(type, totalIntensity / totalOriginIntensity);
					}
				}
				List<String> sortFilenames = new ArrayList<String>(percentMap.keySet());
				Collections.sort(sortFilenames);
				for (String filename : sortFilenames) {
					pwTotalIntensity.print("\t" + filename);
					pwBSeries.print("\t" + filename);
				}
				pwTotalIntensity.println();
				pwBSeries.println();

				for (FragmentationType type : FragmentationType.items) {
					pwTotalIntensity.print(type);
					pwBSeries.print(type);
					for (String filename : sortFilenames) {
						pwTotalIntensity.print("\t" + df.format(percentMap.get(filename).get(type) * 100));
						pwBSeries.print("\t" + df.format(percentMap.get(filename).get(type) / percentMap.get(filename).get(FragmentationType.B) * 100));
					}
					pwTotalIntensity.println();
					pwBSeries.println();
				}
			}
		} finally {
			pwTotalIntensity.close();
			pwBSeries.close();
		}
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
