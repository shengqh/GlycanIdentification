package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.IGlycanWriter;

public abstract class AbstractGlycanFormat implements IGlycanReader,
		IGlycanWriter {

	public Glycan read(String filename) throws IOException {
		Glycan result = new Glycan();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			if (line == null) {
				throw new IllegalArgumentException(filename + " contains nothing!");
			}

			result.setName(line.substring(1).trim());

			line = br.readLine();

			parse(result, line);
		} finally {
			br.close();
		}

		return result;
	}

	public Glycan parse(String line) {
		Glycan result = new Glycan();
		parse(result, line);
		return result;
	}

	public void write(String filename, Glycan glycan) throws IOException {
		PrintWriter pw = new PrintWriter(filename);
		try {
			pw.println(">" + glycan.getName());
			pw.println(glycanToString(glycan));
		} finally {
			pw.close();
		}
	}

	public abstract void parse(Glycan glycan, String line);

	public abstract String glycanToString(Glycan glycan);
}
