package edu.iu.informatics.omics.impl.fragmentation;

import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMassDefinition;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;

public class OligosaccharideFragmentationBBuilder extends
		AbstractNonreducingSeriesMonosaccharideFragmentationBuilder {

	public OligosaccharideFragmentationBBuilder() {
		super();
	}

	public FragmentationType getType() {
		return FragmentationType.B;
	}

	@Override
	protected double buildMz(IMassProxy mp, IMonosaccharide os) {
		final IMassDefinition md = mp.getMassDefinition();
		final Map<Integer, ChemicalGroup> links = os.getLinks();

		double result = mp.getAdduct() - md.getAtom('H');

		result += os.getMass(md);

		int reducingTermPosition = os.getParentReducingTermPosition();

		for (Integer position : links.keySet()) {
			if (reducingTermPosition == position) {
				continue;
			}
			result += os.getLinkMass(mp.getMassDefinition(), position);
		}

		// if the monooligosaccharide is reducing term and
		// there is no modification at reducingTermPosition (usually 1),
		// -OH has been added to total mass. So, we need to reduce a -OH from
		// result.
		if (os.isReducingTerm()
				&& os.containSubGroup(reducingTermPosition, ChemicalGroupFactory.OH)) {
			result -= ChemicalGroupFactory.OH.getMass(md);
		}

		return result;
	}

}
