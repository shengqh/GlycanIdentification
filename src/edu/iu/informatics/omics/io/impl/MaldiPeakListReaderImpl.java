package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;

public class MaldiPeakListReaderImpl extends AbstractPeakListReader<Peak> {
	private static Pattern precursorPattern;

	private static Pattern peakPattern;

	public static Pattern getPrecursorPattern() {
		if (null == precursorPattern) {
			precursorPattern = Pattern.compile("^(?:Precursor\\s+){0,1}([\\d|\\.]+)");
		}
		return precursorPattern;
	}

	public static Pattern getPeakPattern() {
		if (null == peakPattern) {
			peakPattern = Pattern.compile("(\\S+)");
		}
		return peakPattern;
	}

	@Override
	public PeakList<Peak> read(BufferedReader br) throws IOException {
		PeakList<Peak> result = new PeakList<Peak>();

		String line;

		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			} else {
				break;
			}
		}

		if (null == line) {
			throw new IllegalArgumentException("Empty stream.");
		}

		Matcher matcher = getPrecursorPattern().matcher(line);
		while (!matcher.find()) {
			throw new IllegalArgumentException("It's not a valid precursor line. "
					+ line);
		}

		result.setPrecursorAndCharge(Double.parseDouble(matcher.group(1)), 1);

		while ((line = br.readLine()) != null) {
			Peak peak = parsePeak(line);
			if (null != peak) {
				result.getPeaks().add(peak);
			}
		}

		return result;
	}

	public static Peak parsePeak(String line) {
		Matcher matcher = getPeakPattern().matcher(line);
		if (matcher.find()) {
			Peak result = new Peak();
			try {
				result.setMz(Double.parseDouble(matcher.group(1)));
				matcher.find();
				result.setIntensity(Double.parseDouble(matcher.group(1)));
				while (matcher.find()) {
					result.getAnnotations().add(matcher.group(1));
				}
			} catch (Exception ex) {
				return null;
			}
			return result;
		}
		return null;
	}

}
