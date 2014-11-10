package edu.iu.informatics.omics.analysis;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cern.jet.random.sampling.RandomSampler;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.OligosaccharideLinkageType;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class LinearGlycanLinkagePredictorFiveFoldCrossValidation {
	private static final double PEAK_TOLERANCE = 0.3;

	private LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator distanceCalc;

	private String[] dextranFilenames;

	private String[] maltooFilenames;

	private PrintStream psDetails;

	private PrintStream psSummary;

	public LinearGlycanLinkagePredictorFiveFoldCrossValidation(
			IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType,
			AdductType aType, FragmentationType[] fTypes16,
			FragmentationType[] fTypes14, String[] dextranFilenames,
			String[] maltooFilenames, int fromSite) {
		this.distanceCalc = new LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator(
				glycanReader, iType, dType, aType, fTypes16, fTypes14, fromSite);

		this.dextranFilenames = dextranFilenames;

		this.maltooFilenames = maltooFilenames;
	}

	public List<String> process(String resultDirectory) throws Exception {
		String ionTypes = distanceCalc.getIonTypesString();

		String checkFile = resultDirectory + "/" + distanceCalc.getDerivativeType()
				+ "_FiveFoldValidation_ratio_" + ionTypes + "_"
				+ distanceCalc.getFromSite() + ".txt";
		String checkDetailFile = resultDirectory + "/"
				+ distanceCalc.getDerivativeType() + "_FiveFoldValidation_ratio_"
				+ ionTypes + "_" + distanceCalc.getFromSite() + "_detail.txt";

		psDetails = new PrintStream(checkDetailFile);
		psSummary = new PrintStream(checkFile);
		try {
			List<FragmentationTypeOrderEntry> dextran16 = distanceCalc
					.getFragmentationTypeOrderEntryListFromFiles(dextranFilenames, 6,
							PEAK_TOLERANCE);

			List<FragmentationTypeOrderEntry> dextran14 = distanceCalc
					.getFragmentationTypeOrderEntryListFromFiles(dextranFilenames, 4,
							PEAK_TOLERANCE);

			List<FragmentationTypeOrderEntry> matooligsaccharides16 = distanceCalc
					.getFragmentationTypeOrderEntryListFromFiles(maltooFilenames, 6,
							PEAK_TOLERANCE);

			List<FragmentationTypeOrderEntry> matooligsaccharides14 = distanceCalc
					.getFragmentationTypeOrderEntryListFromFiles(maltooFilenames, 4,
							PEAK_TOLERANCE);

			totalCountPosition2 = 0;
			correctCountPosition2 = 0;

			fiveFoldValidate(dextran16, dextran14, matooligsaccharides14,
					matooligsaccharides16);

			if (totalCountPosition2 != 0) {
				psSummary.println();
				psSummary.println("Position 2 positive probability\t"
						+ (double) correctCountPosition2 / (double) totalCountPosition2);
			}

		} finally {
			psDetails.close();
			psSummary.close();
		}

		return Arrays.asList(new String[] { checkFile, checkDetailFile });
	}

	private int totalCountPosition2 = 0;

	private int correctCountPosition2 = 0;

	private double validate(List<FragmentationTypeOrderEntry> sample16,
			List<FragmentationTypeOrderEntry> sample14,
			List<FragmentationTypeOrderEntry> dextranKept16,
			List<FragmentationTypeOrderEntry> matooligsaccharidesKept14,
			TrainingDatasetRatioDistribution scr, List<Double> ratios) {
		FragmentationTypeOrderEntryRatioCalculator calc = new FragmentationTypeOrderEntryRatioCalculator(
				dextranKept16, matooligsaccharidesKept14, scr);

		DecimalFormat df2 = new DecimalFormat("0.0000");
		int c16 = 0;
		int c14 = 0;
		int correctCount = 0;
		for (int i = 0; i < sample16.size(); i++) {
			FragmentationTypeOrderEntry current16 = sample16.get(i);
			FragmentationTypeOrderEntry current14 = sample14.get(i);

			// real linkage has been stored when reading formula from file
			int realLinkage = current16.getInputLinkageType();

			RatioResultEntry value = calc.determine(current16, current14);

			boolean isCorrect = false;

			if (value.getOlType() == OligosaccharideLinkageType.LINK_6) {
				c16++;
				psDetails.print("It's 16");
				if (6 == realLinkage) {
					isCorrect = true;
				}
			} else if (value.getOlType() == OligosaccharideLinkageType.LINK_4) {
				c14++;
				psDetails.print("It's 14");
				if (4 == realLinkage) {
					isCorrect = true;
				}
			} else {
				throw new UnsupportedOperationException();
			}

			if (isCorrect) {
				correctCount++;
			}

			if (2 == sample16.get(i).getPositionFromNonreducingTerm()) {
				totalCountPosition2++;
				if (isCorrect) {
					correctCountPosition2++;
				}
			}

			psDetails.print("\t" + value.getValue16() + "\t" + value.getValue14()
					+ "\t" + df2.format(value.getLogMean16vs14()) + "\t"
					+ df2.format(value.getProb16()) + "\t"
					+ df2.format(value.getProb14()));
			psDetails.println("\t" + current16.getFilename() + "\t"
					+ current16.getPositionFromNonreducingTerm() + "(1,"
					+ current16.getInputLinkageType() + ")");

			ratios.add(value.getLogMean16vs14());
		}

		double result = (double) correctCount / (c16 + c14);
		psDetails.println("Positive probability\t" + result);
		psDetails.println();

		return result;
	}

	private void fiveFoldValidate(List<FragmentationTypeOrderEntry> dextran16,
			List<FragmentationTypeOrderEntry> dextran14,
			List<FragmentationTypeOrderEntry> matooligsaccharides14,
			List<FragmentationTypeOrderEntry> matooligsaccharides16) {
		int count = (int) (dextran16.size() * 0.2);

		double result16 = 0.0;
		double result14 = 0.0;

		List<Double> ratio16 = new ArrayList<Double>();
		List<Double> ratio14 = new ArrayList<Double>();
		int totalTestCount = 1000;
		for (int i = 0; i < totalTestCount; i++) {
			long[] randomDextran = new long[count];
			RandomSampler.sample(count, dextran16.size(), count, 0, randomDextran, 0,
					null);
			long[] randommatooligsaccharides = new long[count];
			RandomSampler.sample(count, dextran16.size(), count, 0,
					randommatooligsaccharides, 0, null);

			List<FragmentationTypeOrderEntry> dextranSample16 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> dextranSample14 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> matooligsaccharidesSample16 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> matooligsaccharidesSample14 = new ArrayList<FragmentationTypeOrderEntry>();

			List<FragmentationTypeOrderEntry> dextranKept16 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> dextranKept14 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> matooligsaccharidesKept16 = new ArrayList<FragmentationTypeOrderEntry>();
			List<FragmentationTypeOrderEntry> matooligsaccharidesKept14 = new ArrayList<FragmentationTypeOrderEntry>();

			split(dextran16, dextranSample16, dextranKept16, randomDextran);
			split(dextran14, dextranSample14, dextranKept14, randomDextran);

			split(matooligsaccharides16, matooligsaccharidesSample16,
					matooligsaccharidesKept16, randommatooligsaccharides);
			split(matooligsaccharides14, matooligsaccharidesSample14,
					matooligsaccharidesKept14, randommatooligsaccharides);

			TrainingDatasetRatioDistribution scr = FragmentationTypeOrderEntryUtils
					.getTrainingDatasetRatioDistribution(dextranKept16, dextranKept14,
							matooligsaccharidesKept16, matooligsaccharidesKept14);

			psDetails.println();
			psDetails.println("------- Test Begin -------");
			psDetails.println(scr);
			result16 += validate(dextranSample16, dextranSample14, dextranKept16,
					matooligsaccharidesKept14, scr, ratio16);

			result14 += validate(matooligsaccharidesSample16,
					matooligsaccharidesSample14, dextranKept16,
					matooligsaccharidesKept14, scr, ratio14);
		}

		result16 /= totalTestCount;
		result14 /= totalTestCount;

		psSummary.println("Average positive 16 rate = " + result16);
		psSummary.println("Average positive 14 rate = " + result14);
	}

	private void split(List<FragmentationTypeOrderEntry> total,
			List<FragmentationTypeOrderEntry> sample,
			List<FragmentationTypeOrderEntry> kept, long[] randoms) {
		for (int i = 0; i < total.size(); i++) {
			boolean found = false;
			for (int j = 0; j < randoms.length; j++) {
				if (i == randoms[j]) {
					found = true;
					break;
				}
			}
			if (found) {
				sample.add(total.get(i));
			} else {
				kept.add(total.get(i));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		IGlycanReader glycanReader = new GlycanColFormat();

		for (int fromSite = 1; fromSite <= 2; fromSite++) {
			new LinearGlycanLinkagePredictorFiveFoldCrossValidation(glycanReader,
					IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na,
					new FragmentationType[] { FragmentationType.A35 },
					new FragmentationType[] { FragmentationType.A35 }, DataLocation
							.getMaldiUnderivatisedDextran(), DataLocation
							.getMaldiUnderivatisedMaltoo(), fromSite)
					.process(DataLocation.maldiUnderivatisedDir);

			new LinearGlycanLinkagePredictorFiveFoldCrossValidation(glycanReader,
					IsotopicType.Average, DerivativeType.Underivatised, AdductType.Na,
					new FragmentationType[] { FragmentationType.A04,
							FragmentationType.A35, FragmentationType.X02 },
					new FragmentationType[] { FragmentationType.A24,
							FragmentationType.A35, FragmentationType.X02 }, DataLocation
							.getMaldiUnderivatisedDextran(), DataLocation
							.getMaldiUnderivatisedMaltoo(), fromSite)
					.process(DataLocation.maldiUnderivatisedDir);

			new LinearGlycanLinkagePredictorFiveFoldCrossValidation(glycanReader,
					IsotopicType.Monoisotopic, DerivativeType.Permethylated,
					AdductType.Na, new FragmentationType[] { FragmentationType.A04 },
					new FragmentationType[] { FragmentationType.A24 }, DataLocation
							.getMaldiPermethylatedDextran(), DataLocation
							.getMaldiPermethylatedMaltoo(), fromSite)
					.process(DataLocation.maldiPermethylatedDir);

			new LinearGlycanLinkagePredictorFiveFoldCrossValidation(glycanReader,
					IsotopicType.Monoisotopic, DerivativeType.Permethylated,
					AdductType.Na, new FragmentationType[] { FragmentationType.A04,
							FragmentationType.A35, FragmentationType.X02 },
					new FragmentationType[] { FragmentationType.A24 }, DataLocation
							.getMaldiPermethylatedDextran(), DataLocation
							.getMaldiPermethylatedMaltoo(), fromSite)
					.process(DataLocation.maldiPermethylatedDir);
		}
	}
}
