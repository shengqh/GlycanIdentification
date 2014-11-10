package edu.iu.informatics.omics.io;

import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;

public interface PeakListWriter <T extends Peak> {
	void write(String filename, PeakList<T> peaks) throws IOException;
	void write(PrintWriter writer, PeakList<T> peaks) throws IOException;
}
