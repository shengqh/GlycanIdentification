package edu.iu.informatics.omics.impl;

import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;

public class Hexose extends AbstractMonosaccharide {
	public Hexose() {
		super("Hexose", "Hex");
	}

	public Hexose(String name, String shortName) {
		super(name, shortName);
	}

	@Override
	protected void doInitCore() {
		core = ChemicalGroupFactory.EMPTY;
	}

	@Override
	protected void doInitSubGroups() {
		Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
		groups.put(1, ChemicalGroupFactory.getCHOH());
		groups.put(2, ChemicalGroupFactory.getCHOH());
		groups.put(3, ChemicalGroupFactory.getCHOH());
		groups.put(4, ChemicalGroupFactory.getCHOH());
		groups.put(5, ChemicalGroupFactory.getCH());
		groups.put(6, ChemicalGroupFactory.getCH2OH());
		groups.put(7, ChemicalGroupFactory.O);
	}

	public int getParentReducingTermPosition(){
		if(getParentReducingTerm() == null){
			return 1;
		}
		
		return getSourcePosition(getParentReducingTerm());
	}
	
	@Override
	protected void doInitLinks() {
//		setLink(1, ChemicalGroupFactory.OH);
	}
}
