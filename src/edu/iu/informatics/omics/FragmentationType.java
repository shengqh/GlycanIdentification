package edu.iu.informatics.omics;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class FragmentationType implements Comparable<FragmentationType> {
	public static FragmentationType A02 = new FragmentationType("0,2A", true);

	public static FragmentationType A03 = new FragmentationType("0,3A", true);

	public static FragmentationType A04 = new FragmentationType("0,4A", true);

	public static FragmentationType A13 = new FragmentationType("1,3A", true);

	public static FragmentationType A14 = new FragmentationType("1,4A", true);

	public static FragmentationType A15 = new FragmentationType("1,5A", true);

	public static FragmentationType A24 = new FragmentationType("2,4A", true);

	public static FragmentationType A25 = new FragmentationType("2,5A", true);

	public static FragmentationType A35 = new FragmentationType("3,5A", true);

	public static FragmentationType B = new FragmentationType("B", true);

	public static FragmentationType C = new FragmentationType("C", true);

	public static FragmentationType X02 = new FragmentationType("0,2X", false);

	public static FragmentationType X03 = new FragmentationType("0,3X", false);

	public static FragmentationType X04 = new FragmentationType("0,4X", false);

	public static FragmentationType X13 = new FragmentationType("1,3X", false);

	public static FragmentationType X14 = new FragmentationType("1,4X", false);

	public static FragmentationType X15 = new FragmentationType("1,5X", false);

	public static FragmentationType X24 = new FragmentationType("2,4X", false);

	public static FragmentationType X25 = new FragmentationType("2,5X", false);

	public static FragmentationType X35 = new FragmentationType("3,5X", false);

	public static FragmentationType Y = new FragmentationType("Y", false);

	public static FragmentationType Z = new FragmentationType("Z", false);

	public static FragmentationType[] items = new FragmentationType[] { A02, A03,
			A04, A13, A14, A15, A24, A25, A35, B, C, X02, X03, X04, X13, X14, X15,
			X24, X25, X35, Y, Z };

	private boolean nonReducingSeries;

	private FragmentationType(String name, boolean nonReducingSeries) {
		super();
		this.name = name;
		this.nonReducingSeries = nonReducingSeries;
	}

	private String name;

	public String getName() {
		return name;
	}

	public int compareTo(FragmentationType myClass) {
		return new CompareToBuilder().append(this.name, myClass.name)
				.toComparison();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FragmentationType)) {
			return false;
		}
		FragmentationType rhs = (FragmentationType) object;
		return new EqualsBuilder().appendSuper(super.equals(object)).append(
				this.name, rhs.name).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(-1817852469, -1111675483).appendSuper(
				super.hashCode()).append(this.name).toHashCode();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public boolean isNonReducingSeries() {
		return nonReducingSeries;
	}

}
