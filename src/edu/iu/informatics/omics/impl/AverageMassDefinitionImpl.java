package edu.iu.informatics.omics.impl;

import cn.ac.rcpa.bio.utils.MassCalculator;
import edu.iu.informatics.omics.AdductType;

public class AverageMassDefinitionImpl extends AbstractMassDefinition {
	public AverageMassDefinitionImpl() {
		super();
	}

	@Override
	protected void initMassMap() {
		protonMassMap.put(AdductType.H, 1.00739);
		protonMassMap.put(AdductType.K, 39.0983);
		protonMassMap.put(AdductType.Na, 22.998977);

		atomMassMap.put('H', MassCalculator.Havg);
		atomMassMap.put('O', MassCalculator.Oavg);
		atomMassMap.put('C', MassCalculator.Cavg);
		atomMassMap.put('N', MassCalculator.Navg);
	}
	
	public String toString(){
		return "Average";
	}
}
