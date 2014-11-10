package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.IMonosaccharide;

public abstract class AbstractNonreducingSeriesMonosaccharideFragmentationBuilder
		extends AbstractMonosaccharideFragmentationBuilder {
	@Override
	protected String getPositionAndNameSuffix(IMonosaccharide os) {
		String nameSuffix = getNameSuffix(os);

		return Integer.toString(os.getRoot().getMaxDistanceToLeaf()
				- os.getDistanceToRoot() + 1)
				+ nameSuffix;
	}
}
