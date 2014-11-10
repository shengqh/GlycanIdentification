package edu.iu.informatics.omics.analysis;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bijnum.BIJStats;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.utils.NormalDistribution;
import cn.ac.rcpa.utils.RcpaCollectionUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.OligosaccharideLinkageType;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class LinearGlycanLinkagePredictorDextrinValidation {
	private static final double PEAK_TOLERANCE = 0.3;

	private LinearGlycanLinkagePredictor predictor;

	private String[] dextrinFilenames;

	private int totalCountPosition2 = 0;

	private int correctCountPosition2 = 0;

	public LinearGlycanLinkagePredictorDextrinValidation(IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType, AdductType aType, FragmentationType[] fTypes16,
			FragmentationType[] fTypes14, String[] dextranFilenames, String[] maltooFilenames, String[] dextrinFilenames, int fromSite) throws Exception {
		this.predictor = new LinearGlycanLinkagePredictor(glycanReader, iType, dType, aType, fTypes16, fTypes14, dextranFilenames, maltooFilenames, fromSite, PEAK_TOLERANCE);
		this.dextrinFilenames = dextrinFilenames;
	}

	public List<String> process(String resultDirectory) throws Exception {

		String checkFile = resultDirectory + "/" + predictor.getDistanceCalc().getDerivativeType() + "_DextrinValidation_ratio_" + predictor.getDistanceCalc().getIonTypesString() + "_"
				+ predictor.getDistanceCalc().getFromSite() + ".txt";
		PrintStream psSummary = new PrintStream(checkFile);
		try {
			List<FragmentationTypeOrderEntry> dextrin16 = predictor.getDistanceCalc().getFragmentationTypeOrderEntryListFromFiles(dextrinFilenames, 6, PEAK_TOLERANCE);

			List<FragmentationTypeOrderEntry> dextrin14 = predictor.getDistanceCalc().getFragmentationTypeOrderEntryListFromFiles(dextrinFilenames, 4, PEAK_TOLERANCE);

			totalCountPosition2 = 0;
			correctCountPosition2 = 0;

			checkDextrinByPredictor(dextrin16, dextrin14, psSummary);

			if (totalCountPosition2 != 0) {
				psSummary.println();
				psSummary.println("Position 2 positive probability\t" + (double) correctCountPosition2 / (double) totalCountPosition2);
			}

		} finally {
			psSummary.close();
		}

		return Arrays.asList(new String[] { checkFile });
	}

	private void checkDextrinByPredictor(List<FragmentationTypeOrderEntry> dextrin16, List<FragmentationTypeOrderEntry> dextrin14, PrintStream psSummary) {
		psSummary.println(predictor.getTrainingDatasetRatioDistribution());

		List<RatioResultEntry> ratioList = predictor.determinate(dextrin16, dextrin14);

		DecimalFormat df = new DecimalFormat("0.0000");

		int c16 = 0;
		int c14 = 0;
		List<Double> ratios = new ArrayList<Double>();

		for (int i = 0; i < ratioList.size(); i++) {
			RatioResultEntry value = ratioList.get(i);
			boolean isCorrect = false;
			if (value.getOlType() == OligosaccharideLinkageType.LINK_6) {
				c16++;
				psSummary.print("It's 16");
			} else if (value.getOlType() == OligosaccharideLinkageType.LINK_4) {
				c14++;
				psSummary.print("It's 14");
				isCorrect = true;
			}

			if (2 == dextrin16.get(i).getPositionFromNonreducingTerm()) {
				totalCountPosition2++;
				if (isCorrect) {
					correctCountPosition2++;
				}
			}

			psSummary.print("\t" + value.getValue16() + "\t" + value.getValue14() + "\t" + df.format(value.getLogMean16vs14()) + "\t" + df.format(value.getProb16()) + "\t"
					+ df.format(value.getProb14()));
			psSummary.println("\t" + dextrin16.get(i).getFilename() + "\t" + dextrin16.get(i).getPositionFromNonreducingTerm() + "(1," + dextrin16.get(i).getInputLinkageType() + ")");

			ratios.add(value.getLogMean16vs14());
		}

		psSummary.println();
		psSummary.println("As16 = " + c16 + "; as14 = " + c14);

		double resultDextrin = (double) c14 / (c16 + c14);
		psSummary.println("Determinated as 14 in dextrin = " + resultDextrin);

		final double[] dextrinRatios = RcpaCollectionUtils.toDoubleArray(ratios);
		final double avg = BIJStats.avg(dextrinRatios);
		final double stdev = BIJStats.stdev(dextrinRatios);

		NormalDistribution nd = new NormalDistribution(avg, stdev, dextrinRatios.length);
		psSummary.println("Ratio distribution = " + nd);
	}

	public static void main(String[] args) throws Exception {
		IGlycanReader glycanReader = new GlycanColFormat();

		for (int fromSite = 1; fromSite <= 2; fromSite++) {
			new LinearGlycanLinkagePredictorDextrinValidation(glycanReader, IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na, new FragmentationType[] { FragmentationType.A35 },
					new FragmentationType[] { FragmentationType.A35 }, DataLocation.getMaldiUnderivatisedDextran(), DataLocation.getMaldiUnderivatisedMaltoo(),
					DataLocation.getMaldiUnderivatisedDextrin(), fromSite).process(DataLocation.maldiUnderivatisedDir);

			new LinearGlycanLinkagePredictorDextrinValidation(glycanReader, IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na, new FragmentationType[] { FragmentationType.A04,
					FragmentationType.A35, FragmentationType.X02 }, new FragmentationType[] { FragmentationType.A24, FragmentationType.A35, FragmentationType.X02 },
					DataLocation.getMaldiUnderivatisedDextran(), DataLocation.getMaldiUnderivatisedMaltoo(), DataLocation.getMaldiUnderivatisedDextrin(), fromSite)
					.process(DataLocation.maldiUnderivatisedDir);

			new LinearGlycanLinkagePredictorDextrinValidation(glycanReader, IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na, new FragmentationType[] { FragmentationType.A04 },
					new FragmentationType[] { FragmentationType.A24 }, DataLocation.getMaldiPermethylatedDextran(), DataLocation.getMaldiPermethylatedMaltoo(),
					DataLocation.getMaldiPermethylatedDextrin(), fromSite).process(DataLocation.maldiPermethylatedDir);

			new LinearGlycanLinkagePredictorDextrinValidation(glycanReader, IsotopicType.Monoisotopic, DerivativeType.Permethylated, AdductType.Na, new FragmentationType[] { FragmentationType.A04,
					FragmentationType.A35, FragmentationType.X02 }, new FragmentationType[] { FragmentationType.A24 }, DataLocation.getMaldiPermethylatedDextran(),
					DataLocation.getMaldiPermethylatedMaltoo(), DataLocation.getMaldiPermethylatedDextrin(), fromSite).process(DataLocation.maldiPermethylatedDir);
		}
	}
}
