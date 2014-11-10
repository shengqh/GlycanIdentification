package edu.iu.informatics.omics.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.MaldiPeak;
import edu.iu.informatics.omics.io.impl.MaldiPeakListExcelReaderImpl;
import edu.iu.informatics.omics.io.impl.MaldiPeakListWriterImpl;

public class MaldiPeakListExcelFileConverter implements IFileProcessor {
	private Pattern pattern = Pattern.compile("(.+glc)(\\d+)_[\\d.]+$");

	private String targetDirectory;

	public MaldiPeakListExcelFileConverter(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public List<String> process(String originFile) throws Exception {
		Map<String, PeakList<MaldiPeak>> peaks = new MaldiPeakListExcelReaderImpl()
				.read(originFile);

		List<String> result = new ArrayList<String>();
		for (String sheetName : peaks.keySet()) {
			Matcher matcher = pattern.matcher(sheetName.replaceAll("-", "_").replace(
					"Glc", "glc"));
			matcher.find();
			String resultFile;

			if (matcher.find()) {
				resultFile = targetDirectory + "/Permethylated_" + matcher.group(1)
						+ StringUtils.leftPad(matcher.group(2), 2, '0') + ".txt.ann";
			} else {
				resultFile = targetDirectory + "/" + sheetName + ".txt.ann";
			}
			new MaldiPeakListWriterImpl<MaldiPeak>().write(resultFile, peaks
					.get(sheetName));
			result.add(resultFile);

		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		File[] files = new File(
				"F:\\sqh\\Project\\glycan\\yehia\\Test\\2nd Paper data")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".xls") && name.startsWith("Permethylated");
					}
				});

		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			new MaldiPeakListExcelFileConverter(
					"F:\\sqh\\Project\\glycan\\yehia\\TestDataset").process(file
					.getAbsolutePath());
		}
	}

}
