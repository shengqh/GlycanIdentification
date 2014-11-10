package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;

public class MaldiPeakListReaderTest extends TestCase {
	private MaldiPeakListReaderImpl reader;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		reader = new MaldiPeakListReaderImpl();
	}

	/*
	 * Test method for
	 * 'edu.iu.informatics.omics.MaldiPeakList.readFromFile(String)'
	 */
	public void testReadFromFile() throws IOException {
		PeakList<Peak> expectMpl = reader.read("data/MaldiPeakList.sample");
		assertEquals(829.21, expectMpl.getPrecursor());
		assertEquals(1, expectMpl.getCharge());
		assertEquals(2, expectMpl.getPeaks().size());
	}

	public void testGetPrecursorPattern() {
		Pattern pattern = MaldiPeakListReaderImpl.getPrecursorPattern();
		Matcher matcher = pattern.matcher("Precursor      1642.30");
		assertTrue(matcher.find());
		assertEquals("1642.30", matcher.group(1));

		Matcher matcher2 = pattern.matcher("1642.30 1");
		assertTrue(matcher2.find());
		assertEquals("1642.30", matcher2.group(1));
	}

	/*
	 * Test method for 'edu.iu.informatics.omics.MaldiPeakList.getPeakPattern()'
	 */
	public void testGetPeakPattern() {
		Pattern pattern = MaldiPeakListReaderImpl.getPeakPattern();
		Matcher matcher = pattern
				.matcher("227.01944    213.85   Z2/0,4A10 0,2X3/B9 Z3/0,4A9 0,2X4/B8 ");
		assertTrue(matcher.find());
		assertEquals("227.01944", matcher.group(1));
		assertTrue(matcher.find());
		assertEquals("213.85", matcher.group(1));
		assertTrue(matcher.find());
		assertEquals("Z2/0,4A10", matcher.group(1));
		assertTrue(matcher.find());
		assertEquals("0,2X3/B9", matcher.group(1));
		assertTrue(matcher.find());
		assertEquals("Z3/0,4A9", matcher.group(1));
		assertTrue(matcher.find());
		assertEquals("0,2X4/B8", matcher.group(1));
		assertFalse(matcher.find());

	}

	public void testParsePeak() {
		Peak actualPeak = MaldiPeakListReaderImpl
				.parsePeak("227.01944    213.85   Z2/0,4A10 0,2X3/B9 Z3/0,4A9 0,2X4/B8 ");
		assertEquals(227.01944, actualPeak.getMz());
		assertEquals(213.85, actualPeak.getIntensity());
		List<String> annotations = Arrays.asList(new String[] { "Z2/0,4A10",
				"0,2X3/B9", "Z3/0,4A9", "0,2X4/B8" });
		assertEquals(annotations, actualPeak.getAnnotations());
	}

}
