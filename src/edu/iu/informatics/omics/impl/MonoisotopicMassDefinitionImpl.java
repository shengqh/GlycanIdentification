package edu.iu.informatics.omics.impl;

import cn.ac.rcpa.bio.utils.MassCalculator;
import edu.iu.informatics.omics.AdductType;

public class MonoisotopicMassDefinitionImpl extends AbstractMassDefinition {
	public MonoisotopicMassDefinitionImpl() {
	}

	@Override
	protected void initMassMap() {
		protonMassMap.put(AdductType.H, 1.00727);
		protonMassMap.put(AdductType.K, 38.963707);
		protonMassMap.put(AdductType.Na, 22.989768);

		atomMassMap.put('H', MassCalculator.Hmono);
		atomMassMap.put('O', MassCalculator.Omono);
		atomMassMap.put('C', MassCalculator.Cmono);
		atomMassMap.put('N', MassCalculator.Nmono);
	}
	
	public String toString(){
		return "Monoisotopic";
	}
}
