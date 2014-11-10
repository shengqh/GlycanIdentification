package edu.iu.informatics.omics.io;

import java.io.BufferedReader;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;

public interface PeakListReader<T extends Peak> {
	PeakList<T> read(String filename) throws Exception;
	
	PeakList<T> read(BufferedReader reader) throws Exception;
}
