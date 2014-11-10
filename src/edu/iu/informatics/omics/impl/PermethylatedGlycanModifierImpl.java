package edu.iu.informatics.omics.impl;

import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.IMonosaccharide;

public class PermethylatedGlycanModifierImpl extends AbstractGlycanModifier {
	
	public void modifyOligosaccharide(IMonosaccharide sa, boolean recursion) {
		if (!recursion) {
			sa.replaceAllSubGroup(ChemicalGroupFactory.OH, ChemicalGroupFactory.OMe);
			sa.replaceAllSubGroup(ChemicalGroupFactory.NAc, ChemicalGroupFactory.NAcMe);
		} else {
			sa.replaceAll(ChemicalGroupFactory.OH, ChemicalGroupFactory.OMe);
			sa.replaceAll(ChemicalGroupFactory.NAc, ChemicalGroupFactory.NAcMe);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Permethylated";
	}

}
