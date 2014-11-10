package edu.iu.informatics.omics;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChemicalGroup {
	List<Character> getAtoms();

	Map<Character, Integer> getAtomCounts();

	ChemicalGroup getCore();

	boolean hasSubGroups();

	Map<Integer, ChemicalGroup> getSubGroups();

	boolean hasLinks();

	Map<Integer, ChemicalGroup> getLinks();

	double getMass(IMassDefinition md);

	double getMassAll(IMassDefinition md);

	double getMassAll(IMassDefinition md, Set<ChemicalGroup> excepts);

	double getLinkMass(IMassDefinition md, int position);

	double getLinkMass(IMassDefinition md, int position, Set<ChemicalGroup> excepts);

	void setLink(int position, ChemicalGroup group);

	void setSubGroup(int position, ChemicalGroup group);

	void removeLink(int position);

	boolean replaceFirstSubGroup(ChemicalGroup source, ChemicalGroup target);

	boolean replaceFirstSubGroup(int position, ChemicalGroup source,
			ChemicalGroup target);

	boolean replaceAllSubGroup(ChemicalGroup source, ChemicalGroup target);

	boolean replaceAll(ChemicalGroup source, ChemicalGroup target);

	boolean replaceAll(ChemicalGroup source, ChemicalGroup target,
			Set<ChemicalGroup> excepts);

	boolean removeFirstSubGroup(ChemicalGroup source);

	boolean removeFirstSubGroup(int position, ChemicalGroup source);

	boolean containSubGroup(ChemicalGroup source);

	boolean containSubGroup(int position, ChemicalGroup source);

	boolean equals(Object object);
}
