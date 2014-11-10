package edu.iu.informatics.omics.io;

import java.io.IOException;

import edu.iu.informatics.omics.Glycan;

public interface IGlycanWriter {
	void write(String filename, Glycan glycan) throws IOException;
}
