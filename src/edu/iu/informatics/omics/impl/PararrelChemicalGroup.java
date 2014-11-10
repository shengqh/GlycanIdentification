package edu.iu.informatics.omics.impl;

import java.util.List;

import edu.iu.informatics.omics.ChemicalGroup;

public class PararrelChemicalGroup extends AbstractCompositeChemicalGroup {
	public PararrelChemicalGroup(List<? extends ChemicalGroup> groups) {
		super(groups);
	}

	public PararrelChemicalGroup(ChemicalGroup... groups) {
		super(groups);
	}
}
