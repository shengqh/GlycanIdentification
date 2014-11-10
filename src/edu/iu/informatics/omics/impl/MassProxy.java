package edu.iu.informatics.omics.impl;

import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IGlycanModifier;
import edu.iu.informatics.omics.IMassDefinition;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;

public class MassProxy implements IMassProxy {
	private IMassDefinition massDefinition;

	private AdductType protonType;

	private IGlycanModifier glycanModifier;

	public MassProxy(IMassDefinition massDefinition,
			IGlycanModifier glycanModifier, AdductType protonType) {
		super();
		this.massDefinition = massDefinition;
		this.glycanModifier = glycanModifier;
		this.protonType = protonType;
	}

	public double getH2O() {
		return getCHO(0, 2, 1);
	}

	public double getAdduct() {
		return massDefinition.getProton(protonType);
	}

	public double getCHO(int C, int H, int O) {
		return massDefinition.getAtom('C') * C + massDefinition.getAtom('H') * H
				+ massDefinition.getAtom('O') * O;
	}

	public double getCHON(int C, int H, int O, int N) {
		return massDefinition.getAtom('C') * C + massDefinition.getAtom('H') * H
				+ massDefinition.getAtom('O') * O + massDefinition.getAtom('N') * N;
	}

	public IMassDefinition getMassDefinition() {
		return massDefinition;
	}

	public void setMassDefinition(IMassDefinition massDefinition) {
		this.massDefinition = massDefinition;
	}

	public AdductType getProtonType() {
		return protonType;
	}

	public void setAdduct(AdductType protonType) {
		this.protonType = protonType;
	}

	public void modifyOligosaccharide(IMonosaccharide sa, boolean recursion) {
		glycanModifier.modifyOligosaccharide(sa, recursion);
	}

	public void modifyGlycan(Glycan glycan) {
		glycanModifier.modifyGlycan(glycan);
	}
	
	public String toString() {
		return "MassType=" + massDefinition.toString() + "\nModifier=" + glycanModifier.toString() + "\nAdduct=" + protonType.toString();
	}
}
