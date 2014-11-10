package edu.iu.informatics.omics.analysis;

import cn.ac.rcpa.utils.NormalDistribution;
import edu.iu.informatics.omics.OligosaccharideLinkageType;

public class RatioResultEntry {
	private String monosaccharideName;
	
	private NormalDistribution value16;

	private NormalDistribution value14;

	private double prob16;

	private double prob14;

	private OligosaccharideLinkageType inputLinkageType;
	
	private OligosaccharideLinkageType olType;

	public OligosaccharideLinkageType getOlType() {
		return olType;
	}

	public void setOlType(OligosaccharideLinkageType olType) {
		this.olType = olType;
	}

	public NormalDistribution getValue14() {
		return value14;
	}

	public void setValue14(NormalDistribution value14) {
		this.value14 = value14;
	}

	public NormalDistribution getValue16() {
		return value16;
	}

	public void setValue16(NormalDistribution value16) {
		this.value16 = value16;
	}

	public double getLogMean16vs14() {
		return Math.log(value16.getMean() / value14.getMean());
	}

	public double getLogProb16vs14() {
		return Math.log(prob16 / prob14);
	}

	public double getProb14() {
		return prob14;
	}

	public void setProb14(double prob14) {
		this.prob14 = prob14;
	}

	public double getProb16() {
		return prob16;
	}

	public void setProb16(double prob16) {
		this.prob16 = prob16;
	}

	public String getMonosaccharideName() {
		return monosaccharideName;
	}

	public void setMonosaccharideName(String monosaccharideName) {
		this.monosaccharideName = monosaccharideName;
	}

	public OligosaccharideLinkageType getInputLinkageType() {
		return inputLinkageType;
	}

	public void setInputLinkageType(OligosaccharideLinkageType inputLinkageType) {
		this.inputLinkageType = inputLinkageType;
	}

}
