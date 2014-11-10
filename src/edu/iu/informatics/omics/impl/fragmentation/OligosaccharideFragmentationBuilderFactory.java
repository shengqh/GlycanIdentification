package edu.iu.informatics.omics.impl.fragmentation;

import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.IMonosaccharideFragmentationBuilder;

public class OligosaccharideFragmentationBuilderFactory {
	private OligosaccharideFragmentationBuilderFactory() {
		super();
	}

	public static IMonosaccharideFragmentationBuilder createHexoseFragmentationBuilder(
			FragmentationType type) {
		if (type == FragmentationType.B) {
			return new OligosaccharideFragmentationBBuilder();
		}
		if (type == FragmentationType.Y) {
			return new OligosaccharideFragmentationYBuilder();
		}
		if (type == FragmentationType.C) {
			return new OligosaccharideFragmentationCBuilder();
		}
		if (type == FragmentationType.Z) {
			return new OligosaccharideFragmentationZBuilder();
		}
		if (type == FragmentationType.A02) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4, 5, 6, 7);
		}
		if (type == FragmentationType.X02) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2);
		}
		if (type == FragmentationType.A03) {
			return new OligosaccharideFragmentationABuilder(type, 4, 5, 6, 7);
		}
		if (type == FragmentationType.X03) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3);
		}
		if (type == FragmentationType.A04) {
			return new OligosaccharideFragmentationABuilder(type, 5, 6, 7);
		}
		if (type == FragmentationType.X04) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 4);
		}
		if (type == FragmentationType.A13) {
			return new OligosaccharideFragmentationABuilder(type, 2, 3);
		}
		if (type == FragmentationType.X13) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 4, 5, 6, 7);
		}
		if (type == FragmentationType.A14) {
			return new OligosaccharideFragmentationABuilder(type, 2, 3, 4);
		}
		if (type == FragmentationType.X14) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 5, 6, 7);
		}
		if (type == FragmentationType.A15) {
			return new OligosaccharideFragmentationABuilder(type, 2, 3, 4, 5, 6);
		}
		if (type == FragmentationType.X15) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 7);
		}
		if (type == FragmentationType.A24) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4);
		}
		if (type == FragmentationType.X24) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 5, 6, 7);
		}
		if (type == FragmentationType.A25) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4, 5, 6);
		}
		if (type == FragmentationType.X25) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 7);
		}
		if (type == FragmentationType.A35) {
			return new OligosaccharideFragmentationABuilder(type, 4, 5, 6);
		}
		if (type == FragmentationType.X35) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 7);
		}

		throw new IllegalArgumentException("Unknown how to create builder of "
				+ type);
	}

	public static IMonosaccharideFragmentationBuilder createNeuFragmentationBuilder(
			FragmentationType type) {
		if (type == FragmentationType.B) {
			return new OligosaccharideFragmentationBBuilder();
		}
		if (type == FragmentationType.Y) {
			return new OligosaccharideFragmentationYBuilder();
		}
		if (type == FragmentationType.C) {
			return new OligosaccharideFragmentationCBuilder();
		}
		if (type == FragmentationType.Z) {
			return new OligosaccharideFragmentationZBuilder();
		}
		if (type == FragmentationType.A02) {
			return new OligosaccharideFragmentationABuilder(type, 4, 5, 6, 7);
		}
		if (type == FragmentationType.X02) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3);
		}
		if (type == FragmentationType.A03) {
			return new OligosaccharideFragmentationABuilder(type, 5, 6, 7);
		}
		if (type == FragmentationType.X03) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 4);
		}
		if (type == FragmentationType.A04) {
			return new OligosaccharideFragmentationABuilder(type, 6, 7);
		}
		if (type == FragmentationType.X04) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 4, 5);
		}
		if (type == FragmentationType.A13) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4);
		}
		if (type == FragmentationType.X13) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 5, 6, 7);
		}
		if (type == FragmentationType.A14) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4, 5);
		}
		if (type == FragmentationType.X14) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 6, 7);
		}
		if (type == FragmentationType.A15) {
			return new OligosaccharideFragmentationABuilder(type, 3, 4, 5, 6);
		}
		if (type == FragmentationType.X15) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 7);
		}
		if (type == FragmentationType.A24) {
			return new OligosaccharideFragmentationABuilder(type, 4, 5);
		}
		if (type == FragmentationType.X24) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 6, 7);
		}
		if (type == FragmentationType.A25) {
			return new OligosaccharideFragmentationABuilder(type, 4, 5, 6);
		}
		if (type == FragmentationType.X25) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 7);
		}
		if (type == FragmentationType.A35) {
			return new OligosaccharideFragmentationABuilder(type, 5, 6);
		}
		if (type == FragmentationType.X35) {
			return new OligosaccharideFragmentationXBuilder(type, 1, 2, 3, 4, 7);
		}

		throw new IllegalArgumentException("Unknown how to create builder of "
				+ type);
	}

}
