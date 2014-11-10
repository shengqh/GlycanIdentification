package edu.iu.informatics.omics.analysis;

import java.util.List;

import cn.ac.rcpa.utils.NormalDistribution;
import edu.csusb.danby.math.ProbMath;
import edu.iu.informatics.omics.OligosaccharideLinkageType;

public class FragmentationTypeOrderEntryRatioCalculator {

	private List<FragmentationTypeOrderEntry> real16;

	private List<FragmentationTypeOrderEntry> real14;

	private TrainingDatasetRatioDistribution scr;

	public FragmentationTypeOrderEntryRatioCalculator(
			List<FragmentationTypeOrderEntry> real16,
			List<FragmentationTypeOrderEntry> real14,
			TrainingDatasetRatioDistribution scr) {
		super();
		this.real16 = real16;
		this.real14 = real14;
		this.scr = scr;
	}

	public RatioResultEntry determine(FragmentationTypeOrderEntry sample16,
			FragmentationTypeOrderEntry sample14) {
		NormalDistribution value16 = FragmentationTypeOrderEntryUtils
				.getRankOrderDistanceDistribution(sample16, real16);
		NormalDistribution value14 = FragmentationTypeOrderEntryUtils
				.getRankOrderDistanceDistribution(sample14, real14);

		double log16vs14 = Math.log(value16.getMean() / value14.getMean());

		double prob16 = 1 - ProbMath.normalCdf((log16vs14 - scr.getDex16()
				.getMean())
				/ scr.getDex16().getSampleVariance());
		double prob14 = ProbMath.normalCdf((log16vs14 - scr.getMat14().getMean())
				/ scr.getMat14().getSampleVariance());

		RatioResultEntry result = new RatioResultEntry();

		result.setMonosaccharideName(sample16.getMonosaccharideName());
		switch (sample16.getInputLinkageType()) {
		case 2:
			result.setInputLinkageType(OligosaccharideLinkageType.LINK_2);
			break;
		case 3:
			result.setInputLinkageType(OligosaccharideLinkageType.LINK_3);
			break;
		case 4:
			result.setInputLinkageType(OligosaccharideLinkageType.LINK_4);
			break;
		case 6:
			result.setInputLinkageType(OligosaccharideLinkageType.LINK_6);
			break;
		default:
			throw new IllegalStateException("Unknow linkage type " + sample16.getInputLinkageType());
		}

		result.setValue16(value16);
		result.setValue14(value14);
		result.setProb16(prob16);
		result.setProb14(prob14);

		if (prob16 > prob14) {
			result.setOlType(OligosaccharideLinkageType.LINK_6);
		} else {
			result.setOlType(OligosaccharideLinkageType.LINK_4);
		}

		return result;
	}

	public TrainingDatasetRatioDistribution getTrainingDatasetRatioDistribution() {
		return scr;
	}
}
