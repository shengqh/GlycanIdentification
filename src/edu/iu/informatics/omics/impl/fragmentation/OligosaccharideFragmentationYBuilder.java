package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMassDefinition;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;

public class OligosaccharideFragmentationYBuilder extends
		AbstractReducingSeriesMonosaccharideFragmentationBuilder {

	public OligosaccharideFragmentationYBuilder() {
		super();
	}

	public FragmentationType getType() {
		return FragmentationType.Y;
	}

	@Override
	protected double buildMz(IMassProxy mp, IMonosaccharide os) {
		if (os.isReducingTerm()) {
			return 0.0;
		}

		final IMassDefinition md = mp.getMassDefinition();
		double result = mp.getAdduct() + md.getAtom('H')
				+ os.getLinkMass(md, os.getParentReducingTermPosition());
		return result;
	}
}
