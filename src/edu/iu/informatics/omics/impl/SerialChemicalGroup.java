package edu.iu.informatics.omics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;

public class SerialChemicalGroup extends AbstractCompositeChemicalGroup {
	public SerialChemicalGroup(ChemicalGroup first, ChemicalGroup next) {
		super(new ChemicalGroup[] { first, next });
	}

	public SerialChemicalGroup(List<? extends ChemicalGroup> groups) {
		super(groups);
	}

	public SerialChemicalGroup(ChemicalGroup... groups) {
		super(groups);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Map<Integer, ChemicalGroup> groups = getSubGroups();
		List<Integer> positions = new ArrayList<Integer>(groups.keySet());
		Collections.sort(positions);

		for (Integer position : positions) {
			ChemicalGroup group = groups.get(position);
			sb.append(group.toString());
		}
		return sb.toString();
	}
}
