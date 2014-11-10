package edu.iu.informatics.omics.io.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.MaldiPeak;

public class MaldiPeakListExcelReaderImpl {

	private Pattern precursorPattern = Pattern.compile("_([\\d.]+)$");

	public Map<String, PeakList<MaldiPeak>> read(String filename)
			throws IOException {
		Map<String, PeakList<MaldiPeak>> result = new HashMap<String, PeakList<MaldiPeak>>();

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filename));

		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet sheet = wb.getSheetAt(sheetIndex);
			String sheetName = wb.getSheetName(sheetIndex).replace('-', '_');

			PeakList<MaldiPeak> pkl = new PeakList<MaldiPeak>();
			Matcher match = precursorPattern.matcher(sheetName);
			if (match.find()) {
				pkl.setPrecursorAndCharge(Double.parseDouble(match.group(1)), 1);
			}

			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				HSSFRow row = sheet.getRow(i);
				MaldiPeak peak = new MaldiPeak();
				peak.setMz(row.getCell((short) 1).getNumericCellValue());
				peak.setLowerBound(row.getCell((short) 2).getNumericCellValue());
				peak.setUpperBound(row.getCell((short) 3).getNumericCellValue());
				peak.setCharge((int) row.getCell((short) 4).getNumericCellValue());
				peak.setIntensity(row.getCell((short) 5).getNumericCellValue());
				peak.setRelativeIntensity(row.getCell((short) 6).getNumericCellValue());
				peak.setArea(row.getCell((short) 7).getNumericCellValue());
				peak.setSnRatio(row.getCell((short) 8).getNumericCellValue());
				peak.setResolution(row.getCell((short) 9).getNumericCellValue());
				peak.setIsotopicClusterArea(row.getCell((short) 10)
						.getNumericCellValue());
				pkl.getPeaks().add(peak);
			}
			result.put(sheetName, pkl);
		}
		return result;
	}

}
