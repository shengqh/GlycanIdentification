package edu.iu.informatics.omics.impl;

import java.util.HashMap;
import java.util.Map;

import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.IMassDefinition;

public abstract class AbstractMassDefinition implements IMassDefinition {
	protected Map<AdductType, Double> protonMassMap = new HashMap<AdductType, Double>();

	protected Map<Character, Double> atomMassMap = new HashMap<Character, Double>();

	public AbstractMassDefinition() {
		super();
		initMassMap();
	}

	protected abstract void initMassMap();

	public final double getProton(AdductType protonType) {
		if (!protonMassMap.containsKey(protonType)) {
			throw new IllegalStateException("Undefined mass of proton type "
					+ protonType);
		}
		return protonMassMap.get(protonType);
	}

	public final double getAtom(Character atom) {
		if (!atomMassMap.containsKey(atom)) {
			throw new IllegalStateException("Undefined mass of atom " + atom);
		}
		return atomMassMap.get(atom);
	}
}
