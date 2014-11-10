package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class MainComponentAnalysisTableBuilder {
	private static final double PEAK_TOLERANCE = 0.3;

	private static IGlycanReader glycanReader = new GlycanColFormat();

	private static PeakListReader<Peak> peakReader = new MaldiPeakListReaderImpl();

	private static class MzPeakMap extends HashMap<String, Peak> {
		private static final long serialVersionUID = 68959988426739137L;

		public MzPeakMap() {
			super();
		}
	}

	private static class FileMzPeakMap extends LinkedHashMap<String, MzPeakMap> {
		private static final long serialVersionUID = -774717168305243025L;

		public FileMzPeakMap() {
			super();
		}

		public boolean AllContains(String mz) {
			for (MzPeakMap mpm : this.values()) {
				if (!mpm.keySet().contains(mz)) {
					return false;
				}
			}
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		anaylsis(DataLocation.maldiUnderivatisedDir + "\\Underivatised_main_component_table.txt", DataLocation.getMaldiUnderivatisedDextran(), DataLocation.getMaldiUnderivatisedMaltoo(),
				DataLocation.getMaldiUnderivatisedDextrin(), IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na);

		anaylsis(DataLocation.maldiPermethylatedDir + "\\Permethylated_main_component_table.txt", DataLocation.getMaldiPermethylatedDextran(), DataLocation.getMaldiPermethylatedMaltoo(),
				DataLocation.getMaldiPermethylatedDextrin(), IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na);
	}

	private static void anaylsis(String resultFilename, String[] dextrans, String[] maltoos, String[] dextrins, IsotopicType iType, DerivativeType dType, AdductType aType) throws Exception,
			FileNotFoundException {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(iType, dType, aType);
		FragmentationBuilder<Peak> builder = new LinearFragmentationBuilderImpl(massProxy);

		Map<String, FileMzPeakMap> typeFilePeakMap = new LinkedHashMap<String, FileMzPeakMap>();

		typeFilePeakMap.put("Dextran", getFilePeakMap(dextrans, builder));
		typeFilePeakMap.put("Maltoo", getFilePeakMap(maltoos, builder));
		typeFilePeakMap.put("Dextrin", getFilePeakMap(dextrins, builder));

		HashSet<String> mzs = new HashSet<String>();
		for (FileMzPeakMap pklMap : typeFilePeakMap.values()) {
			for (MzPeakMap pkl : pklMap.values()) {
				mzs.addAll(pkl.keySet());
			}
		}

		List<String> mzList = new ArrayList<String>();
		for (String mz : mzs) {
			boolean bFound = false;
			for (FileMzPeakMap pklMap : typeFilePeakMap.values()) {
				if (pklMap.AllContains(mz)) {
					bFound = true;
					break;
				}
			}

			if (bFound) {
				mzList.add(mz);
			}
		}

		Collections.sort(mzList, new Comparator<String>() {

			public int compare(String o1, String o2) {
				int result = o1.length() - o2.length();
				if (0 == result) {
					result = o1.compareTo(o2);
				}
				return result;
			}
		});

		PrintWriter pw = new PrintWriter(resultFilename);
		try {
			pw.print("MZ");
			for (FileMzPeakMap maps : typeFilePeakMap.values()) {
				for (String name : maps.keySet()) {
					pw.print("\t" + name);
				}
			}
			pw.println();

			DecimalFormat dfIntensity = new DecimalFormat("0.0000");
			for (String mz : mzList) {
				pw.print(mz);
				for (FileMzPeakMap maps : typeFilePeakMap.values()) {
					for (MzPeakMap peaks : maps.values()) {
						if (peaks.containsKey(mz)) {
							pw.print("\t" + dfIntensity.format(peaks.get(mz).getIntensity()));
						} else {
							pw.print("\t0.0000");
						}
					}
				}
				pw.println();
			}
		} finally {
			pw.close();
		}
	}

	private static FileMzPeakMap getFilePeakMap(String[] dextrans, FragmentationBuilder<Peak> builder) throws Exception {
		FileMzPeakMap filePeakMap = new FileMzPeakMap();

		DecimalFormat dfMz = new DecimalFormat("0.00");
		for (String file : dextrans) {
			String inputPeakFile = RcpaFileUtils.changeExtension(file, "txt.ann");

			PeakList<Peak> inputPeaks = peakReader.read(inputPeakFile);

			normalizePeakIntensity(inputPeaks);

			Glycan inputGlycan = glycanReader.read(file);

			PeakList<Peak> pkl = builder.build(inputGlycan);
			for (Peak p : pkl.getPeaks()) {
				for (Peak trueP : inputPeaks.getPeaks()) {
					double gap = p.getMz() - trueP.getMz();
					if (gap < -PEAK_TOLERANCE) {
						break;
					}

					if (gap > PEAK_TOLERANCE) {
						continue;
					}

					if (p.getIntensity() < trueP.getIntensity()) {
						p.setIntensity(trueP.getIntensity());
					}
				}
			}

			MzPeakMap peakIntensityMap = new MzPeakMap();
			for (Peak p : pkl.getPeaks()) {
				if (p.getIntensity() > 0) {
					peakIntensityMap.put(dfMz.format(p.getMz()), p);
				}
			}

			String filename = RcpaFileUtils.changeExtension(new File(file).getName(), "");

			filePeakMap.put(filename, peakIntensityMap);
		}
		return filePeakMap;
	}

	private static void normalizePeakIntensity(PeakList<Peak> inputPeaks) {
		double totalIntensity = inputPeaks.getTotalIntensity();
		for (Peak p : inputPeaks.getPeaks()) {
			p.setIntensity(p.getIntensity() / totalIntensity);
		}
	}
}
