package edu.iu.informatics.omics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.IMassDefinition;

public abstract class AbstractChemicalGroup implements ChemicalGroup {
	protected Map<Character, Integer> atomCounts = new HashMap<Character, Integer>();

	public AbstractChemicalGroup() {
		super();
	}

	public boolean containSubGroup(ChemicalGroup source) {
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getSubGroups();
			for (ChemicalGroup group : groups.values()) {
				if (group.equals(source) || group.containSubGroup(source)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean containSubGroup(int position, ChemicalGroup source) {
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getSubGroups();
			final ChemicalGroup group = groups.get(position);
			return (null != group)
					&& (group.equals(source) || group.containSubGroup(source));
		}

		return false;
	}

	abstract public ChemicalGroup getCore();

	public double getLinkMass(IMassDefinition md, int position,
			Set<ChemicalGroup> excepts) {
		if (excepts.contains(this)) {
			return 0.0;
		}

		double result = 0.0;
		if (hasLinks()) {
			final ChemicalGroup link = getLinks().get(position);
			if (null != link) {
				try {
					excepts.add(this);
					result += link.getMassAll(md, excepts);
				} finally {
					excepts.remove(this);
				}
			}
		}
		return result;
	}

	public double getLinkMass(IMassDefinition md, int position) {
		return getLinkMass(md, position, new HashSet<ChemicalGroup>());
	}

	public double getMass(IMassDefinition md) {
		double result = 0.0;
		for (Character atom : atomCounts.keySet()) {
			result += md.getAtom(atom) * atomCounts.get(atom);
		}
		return result;
	}

	public double getMassAll(IMassDefinition md, Set<ChemicalGroup> excepts) {
		if (excepts.contains(this)) {
			return 0.0;
		}

		double result = 0.0;
		result += getMass(md);

		if (hasLinks()) {
			try {
				excepts.add(this);
				final Map<Integer, ChemicalGroup> links = getLinks();
				for (ChemicalGroup group : links.values()) {
					result += group.getMassAll(md, excepts);
				}
			} finally {
				excepts.remove(this);
			}
		}
		return result;
	}

	abstract public boolean hasLinks();

	public Map<Integer, ChemicalGroup> getLinks() {
		return getUnmodifiableLinks();
	}

	abstract protected Map<Integer, ChemicalGroup> getModifiableLinks();

	protected Map<Integer, ChemicalGroup> getUnmodifiableLinks() {
		return Collections.unmodifiableMap(getModifiableLinks());
	}

	abstract public boolean hasSubGroups();

	public Map<Integer, ChemicalGroup> getSubGroups() {
		return getUnmodifiableSubGroups();
	}

	abstract protected Map<Integer, ChemicalGroup> getModifiableSubGroups();

	protected Map<Integer, ChemicalGroup> getUnmodifiableSubGroups() {
		return Collections.unmodifiableMap(getModifiableSubGroups());
	}

	public boolean removeFirstSubGroup(ChemicalGroup source) {
		boolean result = false;
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
			final List<Integer> positions = getSortedPosition(groups);
			for (Integer position : positions) {
				final ChemicalGroup group = groups.get(position);
				if (group.equals(source)) {
					groups.remove(position);
					result = true;
					break;
				} else if (group.removeFirstSubGroup(source)) {
					result = true;
					break;
				}
			}
			if (result) {
				resetAtomCounts();
			}
		}
		return result;
	}

	protected void resetAtomCounts() {
		if (this != getCore()) {
			atomCounts.clear();

			if (null != getCore()) {
				atomCounts.putAll(getCore().getAtomCounts());
			}

			if (hasSubGroups()) {
				Map<Integer, ChemicalGroup> groups = getSubGroups();
				for (ChemicalGroup group : groups.values()) {
					Map<Character, Integer> acs = group.getAtomCounts();
					for (Character ch : acs.keySet()) {
						addAtomCount(ch, acs.get(ch));
					}
				}
			}
		}
	}

	public double getMassAll(IMassDefinition md) {
		return getMassAll(md, new HashSet<ChemicalGroup>());
	}

	public boolean removeFirstSubGroup(int position, ChemicalGroup source) {
		boolean result = false;
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
			final ChemicalGroup group = groups.get(position);
			if (null != group) {
				if (group.equals(source)) {
					groups.remove(position);
					result = true;
				} else if (group.removeFirstSubGroup(source)) {
					result = true;
				}
			}
			if (result) {
				resetAtomCounts();
			}
		}
		return result;
	}

	public void removeLink(int position) {
		if (hasLinks()) {
			getModifiableLinks().remove(position);
		}
	}

	private boolean replaceAll(Map<Integer, ChemicalGroup> groups,
			ChemicalGroup source, ChemicalGroup target, Set<ChemicalGroup> excepts) {
		boolean result = false;
		try {
			excepts.add(this);

			for (Integer position : groups.keySet()) {
				if (groups.get(position).equals(source)) {
					if (null == target) {
						groups.remove(position);
					} else {
						groups.put(position, target);
					}
					result = true;
				} else if (groups.get(position).replaceAll(source, target, excepts)) {
					result = true;
				}
			}
		} finally {
			excepts.remove(this);
		}
		return result;
	}

	public boolean replaceAll(ChemicalGroup source, ChemicalGroup target,
			Set<ChemicalGroup> excepts) {
		if (excepts.contains(this)) {
			return false;
		}

		boolean result = replaceAllSubGroup(source, target);

		if (hasLinks()) {
			if (replaceAll(getModifiableLinks(), source, target, excepts)) {
				result = true;
			}
		}
		return result;
	}

	public boolean replaceAll(ChemicalGroup source, ChemicalGroup target) {
		return replaceAll(source, target, new HashSet<ChemicalGroup>());
	}

	public boolean replaceAllSubGroup(ChemicalGroup source, ChemicalGroup target) {
		if (hasSubGroups()) {
			if (replaceAll(getModifiableSubGroups(), source, target,
					new HashSet<ChemicalGroup>())) {
				resetAtomCounts();
				return true;
			}
		}
		return false;
	}

	public boolean replaceFirstSubGroup(ChemicalGroup source, ChemicalGroup target) {
		if (null == target) {
			return removeFirstSubGroup(source);
		}

		boolean result = false;
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
			final List<Integer> positions = getSortedPosition(groups);

			// The chemicalgroup connect directly has high priviage
			for (Integer position : positions) {
				if (groups.get(position).equals(source)) {
					groups.put(position, target);
					result = true;
					break;
				}
			}

			// Then it's turn of chemicalgroup connect undirectly
			if (!result) {
				for (Integer position : positions) {
					if (groups.get(position).replaceFirstSubGroup(source, target)) {
						result = true;
						break;
					}
				}
			}

			if (result) {
				resetAtomCounts();
			}
		}
		return result;
	}

	public boolean replaceFirstSubGroup(int position, ChemicalGroup source,
			ChemicalGroup target) {
		if (null == target) {
			return removeFirstSubGroup(position, source);
		}

		boolean result = false;
		if (hasSubGroups()) {
			final Map<Integer, ChemicalGroup> groups = getModifiableSubGroups();
			final ChemicalGroup group = groups.get(position);
			if (null != group) {
				if (group.equals(source)) {
					groups.put(position, target);
					result = true;
				} else if (group.replaceFirstSubGroup(source, target)) {
					result = true;
				}
			}
			if (result) {
				resetAtomCounts();
			}
		}
		return result;
	}

	public void setLink(int position, ChemicalGroup group) {
		if (null == group) {
			removeLink(position);
		} else {
			if (hasLinks()) {
				getModifiableLinks().put(position, group);
			}
		}
	}

	protected void addAtomCount(Character atom, int count) {
		if (!atomCounts.containsKey(atom)) {
			atomCounts.put(atom, count);
		} else {
			atomCounts.put(atom, atomCounts.get(atom) + count);
		}
	}

	public List<Character> getAtoms() {
		final List<Character> result = new ArrayList<Character>(atomCounts.keySet());
		Collections.sort(result, new Comparator<Character>() {
			public int compare(Character o1, Character o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == 'H') {
					return 1;
				}
				if (o2 == 'H') {
					return -1;
				}
				return o1.compareTo(o2);
			}
		});
		return Collections.unmodifiableList(result);
	}

	public Map<Character, Integer> getAtomCounts() {
		return Collections.unmodifiableMap(atomCounts);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final List<Character> atoms = getAtoms();
		for (Character atom : atoms) {
			int count = atomCounts.get(atom);
			if (1 == count) {
				sb.append(atom.toString());
			} else {
				sb.append(atom.toString() + atomCounts.get(atom));
			}
		}
		return sb.toString();
	}

	protected List<Integer> getSortedPosition(Map<Integer, ChemicalGroup> groups) {
		final ArrayList<Integer> result = new ArrayList<Integer>(groups.keySet());
		Collections.sort(result);
		return result;
	}

	public void setSubGroup(int position, ChemicalGroup group) {
		if (hasSubGroups()) {
			getModifiableSubGroups().put(position, group);
			resetAtomCounts();
		}
	}
}
