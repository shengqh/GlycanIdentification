package edu.iu.informatics.omics.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;

abstract public class AbstractCompositeChemicalGroup extends
		AbstractMultipleChemicalGroup {

	public AbstractCompositeChemicalGroup(List<? extends ChemicalGroup> groups) {
		super(groups);
	}

	public AbstractCompositeChemicalGroup(ChemicalGroup[] groups) {
		super(groups);
	}

	@Override
	public ChemicalGroup getCore() {
		return ChemicalGroupFactory.EMPTY;
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
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof AbstractCompositeChemicalGroup)) {
			return false;
		}
		AbstractCompositeChemicalGroup rhs = (AbstractCompositeChemicalGroup) object;
		return new EqualsBuilder().append(this.subGroups, rhs.subGroups).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(109604291, -1034377633).append(this.subGroups).toHashCode();
	}

}
