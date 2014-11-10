package edu.iu.informatics.omics;

import cn.ac.rcpa.bio.proteomics.Peak;

public class MaldiPeak extends Peak {
	private double lowerBound;

	private double upperBound;

	private int charge;

	private double relativeIntensity;

	private double area;

	private double snRatio;

	private double resolution;

	private double isotopicClusterArea;

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getIsotopicClusterArea() {
		return isotopicClusterArea;
	}

	public void setIsotopicClusterArea(double isotopicClusterArea) {
		this.isotopicClusterArea = isotopicClusterArea;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getRelativeIntensity() {
		return relativeIntensity;
	}

	public void setRelativeIntensity(double relativeIntensity) {
		this.relativeIntensity = relativeIntensity;
	}

	public double getResolution() {
		return resolution;
	}

	public void setResolution(double resolution) {
		this.resolution = resolution;
	}

	public double getSnRatio() {
		return snRatio;
	}

	public void setSnRatio(double snRatio) {
		this.snRatio = snRatio;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

}
