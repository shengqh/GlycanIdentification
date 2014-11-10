package edu.iu.informatics.omics.impl.fragmentation;

import cn.ac.rcpa.bio.proteomics.Peak;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.IMonosaccharideFragmentationBuilder;
import edu.iu.informatics.omics.OligosaccharideUtils;

public abstract class AbstractMonosaccharideFragmentationBuilder implements
		IMonosaccharideFragmentationBuilder {

	public AbstractMonosaccharideFragmentationBuilder() {
		super();
	}

	abstract protected String getPositionAndNameSuffix(IMonosaccharide os);

	abstract protected double buildMz(IMassProxy mp, IMonosaccharide os);

	public Peak build(IMassProxy mp, IMonosaccharide os) {
		String positionAndNameSuffix = getPositionAndNameSuffix(os);

		double mz = buildMz(mp, os);

		Peak result = new Peak();

		result.setMz(mz);
		result.setIntensity(0.0);
		result.getAnnotations().add(getType().getName() + positionAndNameSuffix);

		return result;
	}

	protected String getNameSuffix(IMonosaccharide os) {
		return OligosaccharideUtils.createOrFindNameSuffix(os);
	}

}
