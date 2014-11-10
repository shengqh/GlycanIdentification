package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.io.PeakListWriter;

public class MaldiPeakListWriterImpl<T extends Peak> implements
		PeakListWriter<T> {
	public void write(PrintWriter writer, PeakList<T> peaks) throws IOException {
		DecimalFormat mzDf = new DecimalFormat("0.#####");
		writer.println("Precursor\t" + mzDf.format(peaks.getPrecursor()));
		for (Peak peak : peaks.getPeaks()) {
			writer.println(peak.toString());
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
