package edu.iu.informatics.omics.io.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.MaldiPeak;

public class MaldiPeakListExcelReaderImplTest extends TestCase {

	public void testRead() throws IOException {
		MaldiPeakListExcelReaderImpl reader = new MaldiPeakListExcelReaderImpl();
		Map<String, PeakList<MaldiPeak>> result = reader.read("data/Permethylated_Dextran_6-10.xls");
		assertEquals(5, result.size());
		ArrayList<String> sheets = new ArrayList<String>(result.keySet());
		Collections.sort(sheets);
		assertEquals("Dextran_Glc10_2110.0298",sheets.get(0));
		
		PeakList<MaldiPeak> peaks10 = result.get(sheets.get(0));
		assertEquals(387, peaks10.getPeaks().size());
		assertEquals(56.471172, peaks10.getPeaks().get(0).getMz(), 0.001);
		assertEquals(65, peaks10.getPeaks().get(0).getIntensity(), 0.001);
	}

}
