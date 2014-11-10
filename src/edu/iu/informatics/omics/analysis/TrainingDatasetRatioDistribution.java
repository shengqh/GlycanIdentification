package edu.iu.informatics.omics.analysis;

import cn.ac.rcpa.utils.NormalDistribution;

public class TrainingDatasetRatioDistribution {

	private NormalDistribution dex16;

	private NormalDistribution mat14;

	public NormalDistribution getDex16() {
		return dex16;
	}

	public NormalDistribution getMat14() {
		return mat14;
	}

	public TrainingDatasetRatioDistribution(NormalDistribution dex16,
			NormalDistribution mat14) {
		super();
		this.dex16 = dex16;
		this.mat14 = mat14;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Dex16=" + this.dex16 + "\n");
		sb.append("Mat14=" + this.mat14 + "\n");

		return sb.toString();
	}

}
