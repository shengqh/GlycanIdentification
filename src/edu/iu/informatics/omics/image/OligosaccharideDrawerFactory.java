package edu.iu.informatics.omics.image;

import java.awt.Color;

import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.MonosaccharideType;

public class OligosaccharideDrawerFactory {
	public static IOligosaccharideDrawer create(IMonosaccharide os, int width) {
		String upperName = os.getShortName().toUpperCase();
		for (MonosaccharideType type : MonosaccharideType.values()) {
			if (type.toString().toUpperCase().equals(upperName)) {
				return create(type, width);
			}
		}
		throw new IllegalArgumentException(
				"Don't know how to create Oligosaccharide Drawer of " + os.getName());

	}

	public static IOligosaccharideDrawer create(MonosaccharideType name,
			int width) {
		switch (name) {
		case Hex:
			return new HexoseDrawer(Color.YELLOW, width);
		case HexNAc:
			return new NAcetylhexosamineDrawer(Color.YELLOW, width);
		case Gal:
			return new HexoseDrawer(Color.YELLOW, width);
		case GalNAc:
			return new NAcetylhexosamineDrawer(Color.YELLOW, width);
		case Glc:
			return new HexoseDrawer(Color.BLUE, width);
		case GlcNAc:
			return new NAcetylhexosamineDrawer(Color.BLUE, width);
		case Man:
			return new HexoseDrawer(Color.GREEN, width);
		case ManNAc:
			return new NAcetylhexosamineDrawer(Color.GREEN, width);
		case Xyl:
			return new XyloseDrawer(width);
		case NeuAc:
			return new Neu5AcDrawer(width);
		case Neu5Ac:
			return new Neu5AcDrawer(width);
		case GlcA:
			return new GlcADrawer(width);
		case IdoA:
			return new IdoADrawer(width);
		case Fuc:
			return new FucoseDrawer(width);
		default:
			throw new IllegalArgumentException(
					"Don't know how to create Oligosaccharide drawer of " + name
							+ ", contact author to add it.");
		}
	}

}
