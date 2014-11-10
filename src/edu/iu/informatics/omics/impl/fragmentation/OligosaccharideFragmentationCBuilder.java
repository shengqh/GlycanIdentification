package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;

public class OligosaccharideFragmentationCBuilder extends
AbstractNonreducingSeriesMonosaccharideFragmentationBuilder {
	private OligosaccharideFragmentationBBuilder builder;

	public OligosaccharideFragmentationCBuilder() {
		super();
		builder = new OligosaccharideFragmentationBBuilder();
	}

	public FragmentationType getType() {
		return FragmentationType.C;
	}

	@Override
	protected double buildMz(IMassProxy mp, IMonosaccharide os) {
		return builder.buildMz(mp, os)
				+ ChemicalGroupFactory.H2O.getMass(mp.getMassDefinition());
	}

}
