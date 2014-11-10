package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;

public class OligosaccharideFragmentationZBuilder extends
		AbstractReducingSeriesMonosaccharideFragmentationBuilder {
	private OligosaccharideFragmentationYBuilder builder;

	public OligosaccharideFragmentationZBuilder() {
		super();
		builder = new OligosaccharideFragmentationYBuilder();
	}

	public FragmentationType getType() {
		return FragmentationType.Z;
	}

	@Override
	protected double buildMz(IMassProxy mp, IMonosaccharide os) {
		if (os.isReducingTerm()) {
			return 0.0;
		}
		return builder.buildMz(mp, os)
				- ChemicalGroupFactory.H2O.getMass(mp.getMassDefinition());
	}
}
