package edu.iu.informatics.omics.impl;

import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class NeuAc extends AbstractMonosaccharide {
	public NeuAc() {
		super("N-Acetylneuraminic acid Sialic Acid", "NeuAc");
	}

	public NeuAc(String name) {
		super("N-Acetylneuraminic acid Sialic Acid", name);
	}

	@Override
	protected void doInitCore() {
		core = ChemicalGroupFactory.EMPTY;
	}

	@Override
	protected void doInitSubGroups() {
		Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
		groups.put(1, ChemicalGroupFactory.getCOOH());
		groups.put(2, ChemicalGroupFactory.getCOH());
		groups.put(3, ChemicalGroupFactory.getCH2());
		groups.put(4, ChemicalGroupFactory.getCHOH());
		groups.put(5, ChemicalGroupFactory.getCHNAc());
		groups.put(6, ChemicalGroupFactory.getCHR());
		groups.put(7, ChemicalGroupFactory.O);
	}

	@Override
	protected void doInitLinks() {
	}
	
	public int getParentReducingTermPosition(){
		if(getParentReducingTerm() == null){
			return 2;
		}
		
		return getSourcePosition(getParentReducingTerm());
	}
}
