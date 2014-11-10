package edu.iu.informatics.omics.analysis;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.OligosaccharideLinkageType;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class LinearGlycanLinkagePredictorTestDatasetValidation {
	private static final double PEAK_TOLERANCE = 0.3;

	private LinearGlycanLinkagePredictor predictor;

	private String[] sampleFilenames;

	public LinearGlycanLinkagePredictorTestDatasetValidation(IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType, AdductType aType, FragmentationType[] fTypes16,
			FragmentationType[] fTypes14, String[] dextranFilenames, String[] maltooFilenames, String[] sampleFilenames, int learningFromSite) throws Exception {
		this.predictor = new LinearGlycanLinkagePredictor(glycanReader, iType, dType, aType, fTypes16, fTypes14, dextranFilenames, maltooFilenames, learningFromSite, PEAK_TOLERANCE);
		this.sampleFilenames = sampleFilenames;
	}

	public List<String> process(String resultDirectory) throws Exception {
		predictor.getDistanceCalc().setFromSite(1);

		String checkFile = resultDirectory + "/" + predictor.getDistanceCalc().getDerivativeType() + "_Validation_ratio_" + predictor.getDistanceCalc().getIonTypesString() + "_"
				+ predictor.getDistanceCalc().getFromSite() + ".txt";
		PrintStream psSummary = new PrintStream(checkFile);
		try {
			List<FragmentationTypeOrderEntry> sample16 = predictor.getDistanceCalc().getFragmentationTypeOrderEntryListFromFiles(sampleFilenames, 6, PEAK_TOLERANCE);

			List<FragmentationTypeOrderEntry> sample14 = predictor.getDistanceCalc().getFragmentationTypeOrderEntryListFromFiles(sampleFilenames, 4, PEAK_TOLERANCE);

			for (int i = sample16.size() - 1; i >= 0; i--) {
				if (6 != sample16.get(i).getInputLinkageType() && 4 != sample16.get(i).getInputLinkageType()) {
					sample16.remove(i);
					sample14.remove(i);
				}
			}

			checkByPredictor(sample16, sample14, psSummary);
		} finally {
			psSummary.close();
		}

		return Arrays.asList(new String[] { checkFile });
	}

	private void checkByPredictor(List<FragmentationTypeOrderEntry> sample16, List<FragmentationTypeOrderEntry> sample14, PrintStream psSummary) {
		psSummary.println(predictor.getTrainingDatasetRatioDistribution());

		List<RatioResultEntry> ratioList = predictor.determinate(sample16, sample14);

		DecimalFormat df2 = new DecimalFormat("0.0000");

		List<Double> ratios = new ArrayList<Double>();

		for (int i = 0; i < ratioList.size(); i++) {
			RatioResultEntry value = ratioList.get(i);

			if (value.getOlType() == OligosaccharideLinkageType.LINK_6) {
				psSummary.print("It's 16");
			} else if (value.getOlType() == OligosaccharideLinkageType.LINK_4) {
				psSummary.print("It's 14");
			}

			psSummary.print("\t" + value.getValue16() + "\t" + value.getValue14() + "\t" + df2.format(value.getLogMean16vs14()) + "\t" + df2.format(value.getProb16()) + "\t"
					+ df2.format(value.getProb14()));
			psSummary.println("\t" + sample16.get(i).getFilename() + "\t" + sample16.get(i).getPositionFromNonreducingTerm() + "(1," + sample16.get(i).getInputLinkageType() + ")");

			ratios.add(value.getLogMean16vs14());
		}
	}

	public static void main(String[] args) throws Exception {
		IGlycanReader glycanReader = new GlycanColFormat();

		String sampleDir = "F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\";
		String[] sampleFiles = new String[] { sampleDir + "Permethylated_Tetraose_a.inp", sampleDir + "Permethylated_Tetraose_c.inp", };
		new LinearGlycanLinkagePredictorTestDatasetValidation(glycanReader, IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na, new FragmentationType[] { FragmentationType.A04 },
				new FragmentationType[] { FragmentationType.A24 }, DataLocation.getMaldiPermethylatedDextran(), DataLocation.getMaldiPermethylatedMaltoo(), sampleFiles, 2)
				.process("F:\\sqh\\Project\\glycan\\yehia\\TestDataset\\individual\\");
	}
}
