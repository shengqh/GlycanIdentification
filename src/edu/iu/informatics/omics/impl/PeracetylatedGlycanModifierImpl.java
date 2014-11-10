package edu.iu.informatics.omics.impl;

import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.IMonosaccharide;

public class PeracetylatedGlycanModifierImpl extends AbstractGlycanModifier {
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Peracetylated";
	}

	@Override
	public void modifyOligosaccharide(IMonosaccharide sa, boolean recursion) {
		if (!recursion) {
			sa.replaceAllSubGroup(ChemicalGroupFactory.OH, ChemicalGroupFactory.OAc);
		} else {
			sa.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.OAc);
		}
	}

}
