package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.io.impl.MaldiPeakListWriterImpl;
import edu.iu.informatics.omics.io.impl.MascotGenericFormatReader;

public class ConvertPDData {
	public static void main(String[] args) throws Exception {
		String oldDir = "F:\\sqh\\Project\\glycan\\photofragmentation\\070921";
		String targetDir = "F:\\sqh\\Project\\glycan\\yehia\\PD";

		File[] mgfFiles = new File(oldDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".raw.mgf");
			}
		});

		File targetNativeDir = new File(targetDir, "Underivatised");
		targetNativeDir.mkdir();
		File targetPermethylatedDir = new File(targetDir, "Permethylated");
		targetPermethylatedDir.mkdir();

		MascotGenericFormatReader reader = new MascotGenericFormatReader();
		MaldiPeakListWriterImpl<Peak> writer = new MaldiPeakListWriterImpl<Peak>();

		Pattern p = Pattern.compile("(.+)_(\\d+)_MS2");
		for (File mgfFile : mgfFiles) {
			Matcher m = p.matcher(mgfFile.getName());
			m.find();

			String newFilename = "";

			if (m.group(1).contains("Native")) {
				newFilename = targetNativeDir.getAbsolutePath() + "\\";
			} else {
				newFilename = targetPermethylatedDir.getAbsolutePath()
						+ "\\Permethylated_";
			}

			if (m.group(1).contains("Dextr AN")) {
				newFilename += "Dextran_glc";
			} else if (m.group(1).contains("Dextrin")) {
				newFilename += "Dextrin_glc";
			} else {
				newFilename += "Maltooligosaccharides_glc";
			}

			if (m.group(2).equals("851") || m.group(2).equals("1089")) {
				newFilename += "05";
			} else if (m.group(2).equals("1013") || m.group(2).equals("1293")) {
				newFilename += "06";
			} else if (m.group(2).equals("1175") || m.group(2).equals("1497")) {
				newFilename += "07";
			} else if (m.group(2).equals("1337") || m.group(2).equals("1701")
					|| m.group(2).equals("1702")) {
				newFilename += "08";
			} else if (m.group(2).equals("1499") || m.group(2).equals("1905")
					|| m.group(2).equals("1906")) {
				newFilename += "09";
			} else if (m.group(2).equals("1661") || m.group(2).equals("2110")) {
				newFilename += "10";
			} else {
				continue;
				// throw new IllegalStateException(
				// "I don't know the length of oligosaccharides named as "
				// + mgfFile.getName());
			}
			newFilename += ".txt.ann";

			PeakList<Peak> pkl = reader.read(mgfFile.getAbsolutePath());
			pkl.sort(new Comparator<Peak>() {
				public int compare(Peak o1, Peak o2) {
					return Double.compare(o2.getIntensity(), o1.getIntensity());
				}
			});

			for (int i = 0; i < pkl.getPeaks().size(); i++) {
				for (int j = pkl.getPeaks().size() - 1; j > i; j--) {
					if (Math.abs(pkl.getPeaks().get(i).getMz()
							- pkl.getPeaks().get(j).getMz()) <= 0.8) {
						pkl.getPeaks().remove(j);
					}
				}
			}

			for (int i = 0; i < pkl.getPeaks().size(); i++) {
				double[] mzs = new double[] { pkl.getPeaks().get(i).getMz() - 4.0,
						pkl.getPeaks().get(i).getMz() - 3.0,
						pkl.getPeaks().get(i).getMz() - 2.0,
						pkl.getPeaks().get(i).getMz() - 1.0,
						pkl.getPeaks().get(i).getMz() + 1.0,
						pkl.getPeaks().get(i).getMz() + 2.0,
						pkl.getPeaks().get(i).getMz() + 3.0,
						pkl.getPeaks().get(i).getMz() + 4.0 };
				for (int j = pkl.getPeaks().size() - 1; j > i; j--) {
					for (double mz : mzs) {
						if (Math.abs(pkl.getPeaks().get(j).getMz() - mz) <= 0.2) {
							pkl.getPeaks().remove(j);
							break;
						}
					}
				}
			}

			pkl.sort();

			writer.write(newFilename, pkl);
		}
	}
}
