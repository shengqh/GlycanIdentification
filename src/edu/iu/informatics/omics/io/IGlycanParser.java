package edu.iu.informatics.omics.io;

import edu.iu.informatics.omics.Glycan;

public interface IGlycanParser {
	Glycan parse(String line);

	void parse(Glycan glycan, String line);

	String glycanToString(Glycan glycan);
}
