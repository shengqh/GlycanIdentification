package edu.iu.informatics.omics;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import edu.iu.informatics.omics.impl.AverageMassDefinitionImpl;
import edu.iu.informatics.omics.impl.MassProxy;
import edu.iu.informatics.omics.impl.MonoisotopicMassDefinitionImpl;
import edu.iu.informatics.omics.impl.PeracetylatedGlycanModifierImpl;
import edu.iu.informatics.omics.impl.PermethylatedGlycanModifierImpl;
import edu.iu.informatics.omics.impl.UnderivatisedGlycanModifierImpl;

public class MassProxyFactory {
	private MassProxyFactory() {
	}

	public static IMassProxy getMassProxy(IsotopicType iType,
			DerivativeType derivativeType, AdductType protonType) {

		IMassDefinition md;
		if (iType == IsotopicType.Monoisotopic) {
			md = new MonoisotopicMassDefinitionImpl();
		} else {
			md = new AverageMassDefinitionImpl();
		}

		IGlycanModifier gm;
		switch (derivativeType) {
		case Permethylated:
			gm = new PermethylatedGlycanModifierImpl();
			break;
		case Peracetylated:
			gm = new PeracetylatedGlycanModifierImpl();
			break;
		case Underivatised:
			gm = new UnderivatisedGlycanModifierImpl();
			break;
		default:
			throw new IllegalArgumentException("Cannot find mass proxy implement of "
					+ derivativeType);
		}

		return new MassProxy(md, gm, protonType);
	}
}
