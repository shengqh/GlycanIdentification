package edu.iu.informatics.omics.io;

import java.io.IOException;

import edu.iu.informatics.omics.Glycan;

public interface IGlycanReader extends IGlycanParser {
	Glycan read(String filename) throws IOException;
}
