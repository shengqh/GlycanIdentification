package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.IMonosaccharide;

public abstract class AbstractReducingSeriesMonosaccharideFragmentationBuilder
		extends AbstractMonosaccharideFragmentationBuilder {
	@Override
	protected String getPositionAndNameSuffix(IMonosaccharide os) {
		String nameSuffix = getNameSuffix(os);

		return Integer.toString(os.getDistanceToRoot() - 1) + nameSuffix;
	}
}
