package edu.iu.informatics.omics;

import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;

public interface FragmentationBuilder<T extends Peak> {
	PeakList<T> build(Glycan glycan);

	Map<FragmentationType, List<T>> buildMap(Glycan glycan);

	Map<FragmentationType, List<T>> buildMapWithoutTerminalOligosaccharide(
			Glycan glycan);

	Map<FragmentationType, T> buildMap(IMonosaccharide os);

	Map<String, Peak> buildPeakMap(Glycan glycan);
}
