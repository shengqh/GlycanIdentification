package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.io.PeakListWriter;

public class MaldiPeakListAnnotationWriterImpl<T extends Peak> implements
		PeakListWriter<T> {
	public void write(PrintWriter writer, PeakList<T> peaks) throws IOException {
		DecimalFormat mzDf = new DecimalFormat("0.#####");
		DecimalFormat mz = new DecimalFormat("0");
		writer.println("Precursor\t" + mzDf.format(peaks.getPrecursor()));
		for (Peak peak : peaks.getPeaks()) {
			if (peak.getAnnotations().isEmpty()) {
				writer.println(mzDf.format(peak.getMz()) + "\t"
						+ mzDf.format(peak.getIntensity()));
				continue;
			}

			List<String> annotations = peak.getAnnotations();
			Collections.sort(annotations);

			writer.println(mzDf.format(peak.getMz()) + "\t"
					+ mzDf.format(peak.getIntensity()) + "\t" + mz.format(peak.getMz())
					+ " " + StringUtils.join(annotations.toArray(new String[0]), '/'));
		}
		return;
	}

	public void write(String filename, PeakList<T> peaks) throws IOException {
		PrintWriter pw = new PrintWriter(filename);
		try {
			write(pw, peaks);
		} finally {
			pw.close();
		}
	}
}
