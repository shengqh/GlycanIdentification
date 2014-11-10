package edu.iu.informatics.omics.io.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import edu.iu.informatics.omics.MaldiPeak;

public class MaldiPeakListDetailReaderImplTest extends TestCase {

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.MaldiExcelPeakListReaderImpl.getPrecursorPattern()'
	 */
	public void testGetPrecursorPattern() {
		Pattern pattern = MaldiPeakListDetailReaderImpl.getPrecursorPattern();
		Matcher matcher = pattern
				.matcher("TITLE \"<<Permethylated Dextrin10 - fig2>> 4700 MS/MS Precursor 1293.62 Spec #1[BP = 619.4, 8867]\"");
		assertTrue(matcher.find());
		assertEquals("1293.62", matcher.group(1));
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.MaldiExcelPeakListReaderImpl.getPeakPattern()'
	 */
	public void testGetPeakPattern() {
		Pattern pattern = MaldiPeakListDetailReaderImpl.getPeakPattern();
		Matcher matcher = pattern
				.matcher("1	71.010529	70.88	71.26	1	1150	12.97	25641.63	13	755.85	25748.91");
		assertTrue(matcher.find());
		assertEquals(11, matcher.groupCount());
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.impl.MaldiExcelPeakListReaderImpl.parsePeak(String)'
	 */
	public void testParsePeak() {
		MaldiPeak peak = new MaldiPeakListDetailReaderImpl()
				.parsePeak("1	71.010529	70.88	71.26	1	1150	12.97	25641.63	13	755.85	25748.91");
		assertEquals(71.010529, peak.getMz());
		assertEquals(70.88, peak.getLowerBound());
		assertEquals(71.26, peak.getUpperBound());
		assertEquals(1, peak.getCharge());
		assertEquals(1150.0, peak.getIntensity());
		assertEquals(12.97, peak.getRelativeIntensity());
		assertEquals(25641.63, peak.getArea());
		assertEquals(13.0, peak.getSnRatio());
		assertEquals(755.85, peak.getResolution());
		assertEquals(25748.91, peak.getIsotopicClusterArea());
	}

}
