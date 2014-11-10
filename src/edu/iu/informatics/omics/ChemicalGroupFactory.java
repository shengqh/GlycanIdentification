package edu.iu.informatics.omics;

import edu.iu.informatics.omics.impl.PararrelChemicalGroup;
import edu.iu.informatics.omics.impl.SerialChemicalGroup;
import edu.iu.informatics.omics.impl.SimpleChemicalGroup;

public class ChemicalGroupFactory {
	public static final ChemicalGroup EMPTY = new SimpleChemicalGroup("");

	public static final ChemicalGroup O = new SimpleChemicalGroup("O");

	public static final ChemicalGroup C = new SimpleChemicalGroup("C");

	public static final ChemicalGroup H = new SimpleChemicalGroup("H");

	public static final ChemicalGroup N = new SimpleChemicalGroup("N");

	public static final ChemicalGroup H2O = new SimpleChemicalGroup("H2O");

	public static final ChemicalGroup OH = new SimpleChemicalGroup("OH");

	public static final ChemicalGroup OMe = new SimpleChemicalGroup("OCH3");

	public static final ChemicalGroup OAc = new SimpleChemicalGroup("OCOCH3");

	public static final ChemicalGroup NAc = new SimpleChemicalGroup("NHCOCH3");

	public static final ChemicalGroup NAcMe = new SimpleChemicalGroup("NCH3COCH3");

	public static final ChemicalGroup getR() {
		ChemicalGroup root = ChemicalGroupFactory.getCH2OH();
		ChemicalGroup middle = ChemicalGroupFactory.getCH2OH();
		ChemicalGroup leaf = ChemicalGroupFactory.getCH2OH();
		middle.replaceFirstSubGroup(ChemicalGroupFactory.H, leaf);
		root.replaceFirstSubGroup(ChemicalGroupFactory.H, middle);
		return root;
	}

	private ChemicalGroupFactory() {
	}

	public static final ChemicalGroup create(String formula) {
		return new SimpleChemicalGroup(formula);
	}

	public static final ChemicalGroup getCHOH() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, OH));
	}

	public static final ChemicalGroup getCH2OH() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, H, OH));
	}

	public static final ChemicalGroup getCH2() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, H));
	}

	public static final ChemicalGroup getCH3() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, H, H));
	}

	public static final ChemicalGroup getCH2O() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, H, O));
	}

	public static final ChemicalGroup getCH() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H));
	}

	public static final ChemicalGroup getCOH() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(OH));
	}

	public static final ChemicalGroup getCOOH() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(O, OH));
	}

	public static final ChemicalGroup getCOHCOOH() {
		return new SerialChemicalGroup(ChemicalGroupFactory.C,
				new PararrelChemicalGroup(ChemicalGroupFactory.OH, ChemicalGroupFactory
						.getCOOH()));
	}

	public static final ChemicalGroup getCHR() {
		return new SerialChemicalGroup(C, new PararrelChemicalGroup(H, getR()));
	}

	public static final ChemicalGroup getCHNAc() {
		return new SerialChemicalGroup(ChemicalGroupFactory.C,
				new PararrelChemicalGroup(ChemicalGroupFactory.H,
						ChemicalGroupFactory.NAc));
	}

}
