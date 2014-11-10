package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.mascot.MascotGenericFormatIterator;
import edu.iu.informatics.omics.io.PeakListReader;

public class MascotGenericFormatReader implements PeakListReader<Peak> {
	public PeakList<Peak> read(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		return read(br);
	}

	public PeakList<Peak> read(BufferedReader reader) throws Exception {
		MascotGenericFormatIterator iter = new MascotGenericFormatIterator(reader);
		return iter.next();
	}
}
