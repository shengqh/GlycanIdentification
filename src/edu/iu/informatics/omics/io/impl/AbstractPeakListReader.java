package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.io.PeakListReader;

public abstract class AbstractPeakListReader<T extends Peak> implements PeakListReader<T> {
	public AbstractPeakListReader() {
		super();
	}

	public PeakList<T> read(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			return read(br);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ex.getMessage() + " : " + filename);
		} finally {
			br.close();
		}
	}

	abstract public PeakList<T> read(BufferedReader reader) throws IOException;
}
