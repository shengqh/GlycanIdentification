package edu.iu.informatics.omics.impl;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.iu.informatics.omics.ChemicalGroup;

public class SimpleChemicalGroup extends AbstractChemicalGroup {
	public SimpleChemicalGroup(String atomCounts) {
		super();
		parse(atomCounts);
	}

	final void parse(String formula) {
		for (int i = 0; i < formula.length();) {
			char atom = formula.charAt(i);
			if (!Character.isLetter(atom)) {
				throw new IllegalArgumentException(formula
						+ " is not a valid chemical formula : " + atom + " at position "
						+ i);
			}
			atom = Character.toUpperCase(atom);

			String countStr = "";
			for (int j = i + 1; j < formula.length(); j++) {
				if (Character.isDigit(formula.charAt(j))) {
					countStr += formula.charAt(j);
				} else {
					break;
				}
			}
			i += countStr.length() + 1;
			if (countStr.length() == 0) {
				countStr = "1";
			}
			int count = Integer.parseInt(countStr);
			addAtomCount(atom, count);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof SimpleChemicalGroup)) {
			return false;
		}
		SimpleChemicalGroup rhs = (SimpleChemicalGroup) object;
		return new EqualsBuilder().append(this.atomCounts, rhs.atomCounts)
				.isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(-1777975497, -156495245).append(this.atomCounts).toHashCode();
	}

	@Override
	public ChemicalGroup getCore() {
		return this;
	}

	@Override
	public boolean hasLinks() {
		return false;
	}

	@Override
	protected Map<Integer, ChemicalGroup> getModifiableLinks() {
		throw new UnsupportedOperationException(
				"Call hasLinks before call getModifiableLinks()");
	}

	@Override
	public boolean hasSubGroups() {
		return false;
	}

	@Override
	protected Map<Integer, ChemicalGroup> getModifiableSubGroups() {
		throw new UnsupportedOperationException(
				"Call hasSubGroups before call getModifiableSubGroups()");
	}

}
