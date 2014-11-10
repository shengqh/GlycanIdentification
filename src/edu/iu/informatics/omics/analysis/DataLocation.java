package edu.iu.informatics.omics.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DataLocation {
	public static final String maldiUnderivatisedDir = "F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\Underivatised";

	public static final String maldiPermethylatedDir = "F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\NewPermethylated";

	public static final String pdUnderivatisedDir = "F:\\sqh\\Project\\glycan\\yehia\\PD\\Underivatised";

	public static final String pdPermethylatedDir = "F:\\sqh\\Project\\glycan\\yehia\\PD\\Permethylated";

	public static final String[] getData(String dir, String type, int minLength,
			int maxLength) {
		List<String> result = new ArrayList<String>();
		for (int i = minLength; i <= maxLength; i++) {
			result.add(dir + "/" + type
					+ StringUtils.leftPad(Integer.toString(i), 2, '0') + ".inp");

		}
		return result.toArray(new String[0]);
	}

	public static final String[] getMaldiUnderivatisedDextran() {
		return getData(maldiUnderivatisedDir, "Dextran_glc", 6, 10);
	}

	public static final String[] getMaldiUnderivatisedMaltoo() {
		return getData(maldiUnderivatisedDir, "Maltooligosaccharides_glc", 6, 10);
	}

	public static final String[] getMaldiUnderivatisedDextrin() {
		return getData(maldiUnderivatisedDir, "Dextrin_glc", 6, 10);
	}

	public static final String[] getMaldiPermethylatedDextran() {
		return getData(maldiPermethylatedDir, "Permethylated_Dextran_glc", 6, 10);
	}

	public static final String[] getMaldiPermethylatedMaltoo() {
		return getData(maldiPermethylatedDir,
				"Permethylated_Maltooligosaccharides_glc", 6, 10);
	}

	public static final String[] getMaldiPermethylatedDextrin() {
		return getData(maldiPermethylatedDir, "Permethylated_Dextrin_glc", 6, 10);
	}

	public static final String[] getPDUnderivatisedDextran() {
		return getData(pdUnderivatisedDir, "Dextran_glc", 6, 10);
	}

	public static final String[] getPDUnderivatisedMaltoo() {
		return getData(pdUnderivatisedDir, "Maltooligosaccharides_glc", 6, 10);
	}

	public static final String[] getPDUnderivatisedDextrin() {
		return getData(pdUnderivatisedDir, "Dextrin_glc", 6, 10);
	}

	public static final String[] getPDPermethylatedDextran() {
		return getData(pdPermethylatedDir, "Permethylated_Dextran_glc", 6, 10);
	}

	public static final String[] getPDPermethylatedMaltoo() {
		return getData(pdPermethylatedDir,
				"Permethylated_Maltooligosaccharides_glc", 6, 10);
	}

	public static final String[] getPDPermethylatedDextrin() {
		return getData(pdPermethylatedDir, "Permethylated_Dextrin_glc", 6, 10);
	}

	public static final String[] getPermethylatedTestDataset() {
		List<String> result = new ArrayList<String>();
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated 3_Sialyllactose.inp");
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated 6_Sialyllactose.inp");
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated_Tetraose_c.inp");
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated_Tetraose_a.inp");
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated Dextrin.inp");
		result
				.add("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\Permethylated Maltooligosacchar.inp");
		return result.toArray(new String[0]);
	}

}
