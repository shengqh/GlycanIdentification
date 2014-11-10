package edu.iu.informatics.omics.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.iu.informatics.omics.Glycan;

public class GlycanStructureFileIterator {
	private BufferedReader br;

	private Glycan nextGlycan = null;

	private boolean firstLine = true;

	private Pattern p = Pattern.compile("(\\S+)\\s+(\\S+)");

	private IGlycanParser parser;

	public GlycanStructureFileIterator(IGlycanParser parser, String filename)
			throws IOException {
		this.parser = parser;
		this.br = new BufferedReader(new FileReader(filename));
		this.nextGlycan = readNextGlycan();
	}

	public boolean hasNext() {
		return null != this.nextGlycan;
	}

	public Glycan next() throws IOException {
		Glycan result = this.nextGlycan;
		this.nextGlycan = readNextGlycan();
		return result;
	}

	private Glycan readNextGlycan() throws IOException {
		String line;

		while (true) {
			line = this.br.readLine();
			if (line == null) {
				return null;
			}

			if (line.trim().length() == 0) {
				continue;
			}
			
			break;
		}

		Matcher m = this.p.matcher(line);
		if (!m.find()) {
			return null;
		}

		try {
			Glycan result = this.parser.parse(m.group(2));
			result.setName(m.group(1));
			return result;
		} catch (Exception ex) {
			if (this.firstLine) {
				this.firstLine = false;
				return readNextGlycan();
			}
			throw new IllegalStateException(ex);
		}
	}
}
