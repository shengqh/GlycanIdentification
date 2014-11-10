package edu.iu.informatics.omics.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bijnum.BIJStats;
import cn.ac.rcpa.utils.NormalDistribution;
import cn.ac.rcpa.utils.RcpaCollectionUtils;
import edu.iu.informatics.omics.FragmentationType;

public class FragmentationTypeOrderEntryUtils {
	public static double calculateRankOrderCorrelation_spearman(
			Map<FragmentationType, Integer> order1,
			Map<FragmentationType, Integer> order2) {
		double d2 = 0;
		for (FragmentationType fType : order1.keySet()) {
			int d = order1.get(fType) - order2.get(fType);
			d2 += d * d;
		}

		return 1 - 6 * d2 / (order1.size() * (order1.size() * order1.size() - 1));
	}

	public static double getRankOrderDistance(
			Map<FragmentationType, Integer> order1,
			Map<FragmentationType, Integer> order2) {
		double d2 = 0;
		for (FragmentationType fType : order1.keySet()) {
			int d = order1.get(fType) - order2.get(fType);
			d2 += d * d;
		}

		return Math.sqrt(d2 / order1.size());
	}

	public static NormalDistribution getRankOrderDistanceDistribution(
			FragmentationTypeOrderEntry order1,
			List<FragmentationTypeOrderEntry> orderList2) {
		List<Double> distanceList = new ArrayList<Double>();
		for (int j = 0; j < orderList2.size(); j++) {
			if (order1 == orderList2.get(j)) {
				continue;
			}
			double rs2 = getRankOrderDistance(order1.getTypeOrder(), orderList2
					.get(j).getTypeOrder());
			distanceList.add(rs2);
		}

		final double[] distances = RcpaCollectionUtils.toDoubleArray(distanceList);
		final double avg = BIJStats.avg(distances);
		final double stdev = BIJStats.stdev(distances);

		return new NormalDistribution(avg, stdev, distances.length);
	}

	private static NormalDistribution getRatioDistribution(
			List<FragmentationTypeOrderEntry> sample16,
			List<FragmentationTypeOrderEntry> sample14,
			List<FragmentationTypeOrderEntry> real16,
			List<FragmentationTypeOrderEntry> real14) {
		List<Double> ratioList = new ArrayList<Double>();
		for (int i = 0; i < sample16.size(); i++) {
			NormalDistribution corr16 = getRankOrderDistanceDistribution(sample16
					.get(i), real16);

			NormalDistribution corr14 = getRankOrderDistanceDistribution(sample14
					.get(i), real14);

			ratioList.add(Math.log(corr16.getMean() / corr14.getMean()));
		}

		double[] ratios = RcpaCollectionUtils.toDoubleArray(ratioList);
		final double stdev = BIJStats.stdev(ratios);
		final double avg = BIJStats.avg(ratios);

		return new NormalDistribution(avg, stdev, ratios.length);
	}

	public static TrainingDatasetRatioDistribution getTrainingDatasetRatioDistribution(
			List<FragmentationTypeOrderEntry> real16,
			List<FragmentationTypeOrderEntry> real16as14,
			List<FragmentationTypeOrderEntry> real14as16,
			List<FragmentationTypeOrderEntry> real14) {

		NormalDistribution true16 = getRatioDistribution(real16, real16as14,
				real16, real14);
		NormalDistribution true14 = getRatioDistribution(real14as16, real14,
				real16, real14);
		TrainingDatasetRatioDistribution result = new TrainingDatasetRatioDistribution(
				true16, true14);

		return result;
	}

}
