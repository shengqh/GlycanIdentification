package edu.iu.informatics.omics.analysis;

import java.util.Map;

import edu.iu.informatics.omics.FragmentationType;

public class FragmentationTypeOrderEntry {

	public FragmentationTypeOrderEntry() {
		super();
	}

	private String filename;

	private int positionFromNonreducingTerm;
	
	private String monosaccharideName;

	private int inputLinkageType;

	private Map<FragmentationType, Integer> typeOrder;

	public int getInputLinkageType() {
		return inputLinkageType;
	}

	public void setInputLinkageType(int inputLinkageType) {
		this.inputLinkageType = inputLinkageType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getPositionFromNonreducingTerm() {
		return positionFromNonreducingTerm;
	}

	public void setPositionFromNonreducingTerm(int positionFromNonreducing) {
		this.positionFromNonreducingTerm = positionFromNonreducing;
	}

	public Map<FragmentationType, Integer> getTypeOrder() {
		return typeOrder;
	}

	public void setTypeOrder(Map<FragmentationType, Integer> typeOrder) {
		this.typeOrder = typeOrder;
	}

	public String getMonosaccharideName() {
		return monosaccharideName;
	}

	public void setMonosaccharideName(String monosaccharideName) {
		this.monosaccharideName = monosaccharideName;
	}

}
