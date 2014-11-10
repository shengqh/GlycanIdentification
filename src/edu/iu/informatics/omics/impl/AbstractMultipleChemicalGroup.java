package edu.iu.informatics.omics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;

public abstract class AbstractMultipleChemicalGroup extends
		AbstractChemicalGroup {
	Map<Integer, ChemicalGroup> subGroups = new HashMap<Integer, ChemicalGroup>();

	public AbstractMultipleChemicalGroup(ChemicalGroup... groups) {
		super();
		for (int i = 0; i < groups.length; i++) {
			this.subGroups.put(i, groups[i]);
		}
		resetAtomCounts();
	}

	public AbstractMultipleChemicalGroup(List<? extends ChemicalGroup> groups) {
		super();
		for (int i = 0; i < groups.size(); i++) {
			this.subGroups.put(i, groups.get(i));
		}
		resetAtomCounts();
	}

	@Override
	protected Map<Integer, ChemicalGroup> getModifiableSubGroups() {
		return subGroups;
	}

	@Override
	public boolean hasSubGroups() {
		return true;
	}
}
