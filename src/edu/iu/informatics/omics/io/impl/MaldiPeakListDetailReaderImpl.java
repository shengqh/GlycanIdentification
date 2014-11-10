package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.MaldiPeak;

public class MaldiPeakListDetailReaderImpl extends
		AbstractPeakListReader<MaldiPeak> {
	private static Pattern precursorPattern;

	private static Pattern peakPattern;

	public static Pattern getPrecursorPattern() {
		if (null == precursorPattern) {
			precursorPattern = Pattern.compile("Precursor\\s+([\\d|\\.]+)");
		}
		return precursorPattern;
	}

	public static Pattern getPeakPattern() {
		if (null == peakPattern) {
			peakPattern = Pattern
					.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)");
		}
		return peakPattern;
	}

	@Override
	public PeakList<MaldiPeak> read(String filename) throws IOException {
		PeakList<MaldiPeak> result = new PeakList<MaldiPeak>();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			if (line == null) {
				throw new IllegalArgumentException(filename + " contains nothing!");
			}
			Matcher matcher = getPrecursorPattern().matcher(line);
			if (!matcher.find()) {
				throw new IllegalArgumentException(line + " is not a precursor line!");
			}

			result.setPrecursorAndCharge(Double.parseDouble(matcher.group(1)), 1);
		} finally {
			br.close();
		}

		String detailFile = RcpaFileUtils.changeExtension(filename, "detail");
		BufferedReader brDetail = new BufferedReader(new FileReader(detailFile));
		brDetail.readLine();
		try {
			String line;
			while ((line = brDetail.readLine()) != null) {
				MaldiPeak peak = parsePeak(line);
				if (null != peak) {
					result.getPeaks().add(peak);
				}
			}
		} finally {
			brDetail.close();
		}

		return result;
	}

	public MaldiPeak parsePeak(String line) {
		Matcher matcher = getPeakPattern().matcher(line);
		if (matcher.find()) {
			MaldiPeak result = new MaldiPeak();
			try {
				result.setMz(Double.parseDouble(matcher.group(2)));
				result.setLowerBound(Double.parseDouble(matcher.group(3)));
				result.setUpperBound(Double.parseDouble(matcher.group(4)));
				result.setCharge(Integer.parseInt(matcher.group(5)));
				result.setIntensity(Double.parseDouble(matcher.group(6)));
				result.setRelativeIntensity(Double.parseDouble(matcher.group(7)));
				result.setArea(Double.parseDouble(matcher.group(8)));
				result.setSnRatio(Double.parseDouble(matcher.group(9)));
				result.setResolution(Double.parseDouble(matcher.group(10)));
				result.setIsotopicClusterArea(Double.parseDouble(matcher.group(11)));
			} catch (Exception ex) {
				return null;
			}
			return result;
		}
		return null;
	}

	@Override
	public PeakList<MaldiPeak> read(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException(
				"Call read(String filename) please.");
	}

}
