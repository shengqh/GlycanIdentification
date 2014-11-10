package edu.iu.informatics.omics.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.iu.informatics.omics.Glycan;

public class GlycanIOUtils {
	public static List<Glycan> readGlycanStructures(IGlycanParser parser,
			String glycanStrFile) throws IOException {
		GlycanStructureFileIterator iter = new GlycanStructureFileIterator(parser,
				glycanStrFile);

		List<Glycan> result = new ArrayList<Glycan>();
		while (iter.hasNext()) {
			result.add(iter.next());
		}

		return result;
	}

}
