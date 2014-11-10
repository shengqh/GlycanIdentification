package edu.iu.informatics.omics;

import cn.ac.rcpa.bio.proteomics.Peak;

public interface IMonosaccharideFragmentationBuilder {
	FragmentationType getType();

	Peak build(IMassProxy mp, IMonosaccharide os);
}
