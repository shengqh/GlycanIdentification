package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.OligosaccharideUtils;

public class OligosaccharideFragmentationABuilder extends
		AbstractNonreducingSeriesMonosaccharideFragmentationBuilder {
	private Integer[] positions;

	private FragmentationType type;

	public OligosaccharideFragmentationABuilder(FragmentationType type,
			Integer... positions) {
		super();
		this.type = type;
		this.positions = positions;
	}

	public FragmentationType getType() {
		return this.type;
	}

	@Override
	protected double buildMz(IMassProxy mp, IMonosaccharide os) {
		return OligosaccharideUtils.getFragmentation(mp, os, positions);
	}

}
