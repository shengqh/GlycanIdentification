package edu.iu.informatics.omics.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.io.IGlycanReader;

public class LinearGlycanLinkagePredictor {
	private LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator distanceCalc;

	private FragmentationTypeOrderEntryRatioCalculator ratioCalc;

	public LinearGlycanLinkagePredictor(IGlycanReader glycanReader,
			IsotopicType iType, DerivativeType dType, AdductType aType,
			FragmentationType[] fTypes16, FragmentationType[] fTypes14,
			String[] dextranFilenames, String[] maltooFilenames,
			int learningFromSite, double learningDatasetTolerance) throws Exception {
		this.distanceCalc = new LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator(
				glycanReader, iType, dType, aType, fTypes16, fTypes14, learningFromSite);

		initializeCalc(dextranFilenames, maltooFilenames, learningDatasetTolerance);
	}

	private void initializeCalc(String[] dextranFilenames,
			String[] maltooFilenames, double learningDatasetTolerance)
			throws Exception {
		List<FragmentationTypeOrderEntry> dextran16 = distanceCalc
				.getFragmentationTypeOrderEntryListFromFiles(dextranFilenames, 6,
						learningDatasetTolerance);

		List<FragmentationTypeOrderEntry> dextran14 = distanceCalc
				.getFragmentationTypeOrderEntryListFromFiles(dextranFilenames, 4,
						learningDatasetTolerance);

		List<FragmentationTypeOrderEntry> matooligsaccharides16 = distanceCalc
				.getFragmentationTypeOrderEntryListFromFiles(maltooFilenames, 6,
						learningDatasetTolerance);

		List<FragmentationTypeOrderEntry> matooligsaccharides14 = distanceCalc
				.getFragmentationTypeOrderEntryListFromFiles(maltooFilenames, 4,
						learningDatasetTolerance);

		TrainingDatasetRatioDistribution scr = FragmentationTypeOrderEntryUtils
				.getTrainingDatasetRatioDistribution(dextran16, dextran14,
						matooligsaccharides16, matooligsaccharides14);

		this.ratioCalc = new FragmentationTypeOrderEntryRatioCalculator(dextran16,
				matooligsaccharides14, scr);
	}

	public Map<Integer, RatioResultEntry> determinate(String formula,
			PeakList<Peak> inputPeaks, int testFromSite, double testPeakTolerance)
			throws Exception {
		distanceCalc.setFromSite(testFromSite);

		Glycan inputGlycan = distanceCalc.getGlycanReader().parse(formula);

		List<FragmentationTypeOrderEntry> sample16 = distanceCalc
				.getFragmentationTypeOrderEntryList(inputGlycan, inputPeaks, 6,
						testPeakTolerance);

		List<FragmentationTypeOrderEntry> sample14 = distanceCalc
				.getFragmentationTypeOrderEntryList(inputGlycan, inputPeaks, 4,
						testPeakTolerance);

		List<RatioResultEntry> ratios = determinate(sample16, sample14);

		Map<Integer, RatioResultEntry> result = new HashMap<Integer, RatioResultEntry>();
		for (int i = 0; i < ratios.size(); i++) {
			result.put(sample16.get(i).getPositionFromNonreducingTerm(), ratios
					.get(i));
		}

		return result;
	}

	public List<RatioResultEntry> determinate(
			List<FragmentationTypeOrderEntry> sample16,
			List<FragmentationTypeOrderEntry> sample14) {
		List<RatioResultEntry> result = new ArrayList<RatioResultEntry>();

		for (int i = 0; i < sample16.size(); i++) {
			FragmentationTypeOrderEntry current16 = sample16.get(i);

			FragmentationTypeOrderEntry current14 = sample14.get(i);

			result.add(ratioCalc.determine(current16, current14));
		}

		return result;
	}

	public TrainingDatasetRatioDistribution getTrainingDatasetRatioDistribution() {
		return ratioCalc.getTrainingDatasetRatioDistribution();
	}

	public LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator getDistanceCalc() {
		return distanceCalc;
	}

	public FragmentationTypeOrderEntryRatioCalculator getRatioCalc() {
		return ratioCalc;
	}
}
