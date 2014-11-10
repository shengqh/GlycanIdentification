package edu.iu.informatics.omics.impl;

import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class Fucose extends AbstractMonosaccharide {
	public Fucose() {
		super("Fucose", "Fuc");
	}

	@Override
	protected void doInitCore() {
		core = ChemicalGroupFactory.EMPTY;
	}

	@Override
	protected void doInitSubGroups() {
		Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
		groups.put(1, ChemicalGroupFactory.getCOH());
		groups.put(2, ChemicalGroupFactory.getCOH());
		groups.put(3, ChemicalGroupFactory.getCOH());
		groups.put(4, ChemicalGroupFactory.getCOH());
		groups.put(5, ChemicalGroupFactory.getCH2());
		groups.put(6, ChemicalGroupFactory.O);
	}

	@Override
	protected void doInitLinks() {
	}
	
	public int getParentReducingTermPosition(){
		if(getParentReducingTerm() == null){
			return 1;
		}
		
		return getSourcePosition(getParentReducingTerm());
	}
}
