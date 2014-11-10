package edu.iu.informatics.omics;

import edu.iu.informatics.omics.impl.Fucose;
import edu.iu.informatics.omics.impl.Hexose;
import edu.iu.informatics.omics.impl.NeuAc;

public class MonosaccharideFactory {
	private MonosaccharideFactory() {
		super();
	}

	public static IMonosaccharide create(String name) {
		String upperName = name.toUpperCase();
		for (MonosaccharideType type : MonosaccharideType.values()) {
			if (type.toString().toUpperCase().equals(upperName)) {
				return create(type);
			}
		}
		throw new IllegalArgumentException(
				"Don't know how to create Oligosaccharide " + name);
	}

	public static IMonosaccharide create(MonosaccharideType name) {
		switch (name) {
		case Hex:
			return createHex("Hexose", "Hex");
		case HexNAc:
			return createHexNAc("HexNAc", "HexNAc");
		case Glc:
			return createHex("Glucose", "Glc");
		case GlcNAc:
			return createHexNAc("N-acetylglucosamine", "GlcNAc");
		case Man:
			return createHex("Mannose", "Man");
		case ManNAc:
			return createHexNAc("N-acetylmannosamine", "ManNAc");
		case Gal:
			return createHex("Galactose", "Gal");
		case GalNAc:
			return createHexNAc("N-acetylgalactosamine", "GalNAc");
		case Xyl:
			return createXyl();
		case NeuAc:
			return new NeuAc();
		case Neu5Ac:
			return new NeuAc("Neu5Ac");
		case GlcA:
			throw new UnsupportedOperationException();
		case IdoA:
			throw new UnsupportedOperationException();
		case Fuc:
			return new Fucose();
		default:
			throw new IllegalArgumentException(
					"Don't know how to create Oligosaccharide " + name
							+ ", contact author to add it.");
		}
	}

	private static IMonosaccharide createXyl() {
		Hexose result = new Hexose("Xylose", "Xyl");

		result.replaceFirstSubGroup(6, ChemicalGroupFactory.getCH2OH(),
				ChemicalGroupFactory.H);

		return result;
	}

	public static IMonosaccharide createHex(String name, String shortName) {
		return new Hexose(name, shortName);
	}

	public static IMonosaccharide createHexNAc(String name, String shortName) {
		Hexose result = new Hexose(name, shortName);
		result.replaceFirstSubGroup(2, ChemicalGroupFactory.OH,
				ChemicalGroupFactory.NAc);
		return result;
	}
}
