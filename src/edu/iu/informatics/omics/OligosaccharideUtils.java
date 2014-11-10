package edu.iu.informatics.omics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.iu.informatics.omics.impl.Fucose;

public final class OligosaccharideUtils {
	private static final String NAME_SUFFIX = "NAME_SUFFIX";

	private static List<String[]> nameSuffixLevels;

	static {
		nameSuffixLevels = new ArrayList<String[]>();
		nameSuffixLevels.add(new String[] { "a", "b", "c", "d" });
		nameSuffixLevels.add(new String[] { "'", "\"", "'''", "''''" });
	}

	private OligosaccharideUtils() {
		super();
	}

	public static String createOrFindNameSuffix(IMonosaccharide os) {
		String nameSuffix = getNameSuffix(os);

		if (null == nameSuffix) {
			OligosaccharideUtils.initializeNameSuffix(os.getRoot());
			nameSuffix = OligosaccharideUtils.getNameSuffix(os);
		}

		return nameSuffix;
	}

	public static String getNameSuffix(IMonosaccharide os) {
		if (os.getAnnotations().containsKey(NAME_SUFFIX)) {
			return (String) os.getAnnotations().get(NAME_SUFFIX);
		}

		return null;
	}

	public static void setNameSuffix(IMonosaccharide os, String nameIndex) {
		os.getAnnotations().put(NAME_SUFFIX, nameIndex);
	}

	/**
	 * Calculate the maximum depth of monosaccharide and its children. The depth
	 * of tree root is 1, the depths of children except fucose of tree root are 2,
	 * and so on.
	 * 
	 * @param os
	 * @return depth
	 */
	public static int getMaxDepth(IMonosaccharide os) {
		int result = 1;

		int subDepth = 0;
		Collection<IMonosaccharide> oss = os.getSubMonosaccharides().values();
		for (IMonosaccharide subos : oss) {
			if (subos instanceof Fucose) {
				continue;
			}

			int curDepth = getMaxDepth(subos);
			if (subDepth < curDepth) {
				subDepth = curDepth;
			}
		}
		return result + subDepth;
	}

	public static double getFragmentation(IMassProxy mp, IMonosaccharide os,
			Integer... positions) {
		final IMassDefinition md = mp.getMassDefinition();
		final Map<Integer, ChemicalGroup> links = os.getLinks();

		final Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		boolean bFindOne = false;
		boolean bFindAll = true;
		for (Integer position : oss.keySet()) {
			boolean bFound = false;
			for (Integer p : positions) {
				if (position == p) {
					bFound = true;
					break;
				}
			}
			if (bFound) {
				bFindOne = true;
			} else {
				bFindAll = false;
			}
		}

		if ((!bFindOne || bFindAll) && !os.isReducingTerm()
				&& !os.isNonreducingTerm()) {
			return 0.0;
		}

		double result = 0.0;

		// add the mass of chemical group of current monosaccharide
		for (Integer position : positions) {
			if (links.containsKey(position)) {
				result += os.getLinkMass(md, position);
			}
		}

		// add the mass of chemical group linked to current monosaccharide
		final Map<Integer, ChemicalGroup> subGroups = os.getSubGroups();
		for (Integer position : positions) {
			if (subGroups.containsKey(position)) {
				result += subGroups.get(position).getMassAll(md);
			}
		}

		// add the adduct and core mass of current monosaccharide
		result += mp.getAdduct() + os.getCore().getMass(md);

		return result;
	}

	public static double getFragmentation(IMassProxy mp, IMonosaccharide os1,
			int[] positions1, IMonosaccharide os2, int[] positions2) {
		final IMassDefinition md = mp.getMassDefinition();
		final Map<Integer, ChemicalGroup> links = os1.getLinks();

		final Map<Integer, IMonosaccharide> oss = os1.getMonosaccharides();
		boolean bFindOne = false;
		boolean bFindAll = true;
		for (Integer position : oss.keySet()) {
			boolean bFound = false;
			for (Integer p : positions1) {
				if (position == p) {
					bFound = true;
					break;
				}
			}
			if (bFound) {
				bFindOne = true;
			} else {
				bFindAll = false;
			}
		}

		if ((!bFindOne || bFindAll) && !os1.isReducingTerm()
				&& !os1.isNonreducingTerm()) {
			return 0.0;
		}

		double result = 0.0;
		for (Integer position : positions1) {
			if (links.containsKey(position)) {
				result += os1.getLinkMass(md, position);
			}
		}

		final Map<Integer, ChemicalGroup> subGroups = os1.getSubGroups();
		for (Integer position : positions1) {
			if (subGroups.containsKey(position)) {
				result += subGroups.get(position).getMassAll(md);
			}
		}

		result += mp.getAdduct() + os1.getCore().getMass(md);

		return result;
	}

	public static void initializeNameSuffix(IMonosaccharide reducingTerm) {
		String rootSuffix = "";
		setNameSuffix(reducingTerm, "");
		initializeSubTreeNameSuffix(reducingTerm, rootSuffix, -1);
	}

	private static void initializeSubTreeNameSuffix(IMonosaccharide currentMs,
			String parentSuffix, int parentSuffixLevel) {
		Map<Integer, IMonosaccharide> subMss = currentMs.getSubMonosaccharides();
		if (0 == subMss.size()) {
			return;
		}

		if (1 == subMss.size()) {
			IMonosaccharide subMs = subMss.values().iterator().next();
			OligosaccharideUtils.setNameSuffix(subMs, parentSuffix);
			initializeSubTreeNameSuffix(subMs, parentSuffix, parentSuffixLevel);
			return;
		}

		int subSuffixLevel = parentSuffixLevel + 1;

		List<IMonosaccharide> mss = new ArrayList<IMonosaccharide>(subMss.values());

		sortByMaxDistanceToLeafAndReducingTermPosition(mss);

		String[] subSuffixLevels = nameSuffixLevels.get(subSuffixLevel);
		for (int i = 0; i < mss.size(); i++) {
			IMonosaccharide subMs = mss.get(i);
			String subSuffix = parentSuffix + subSuffixLevels[i];
			OligosaccharideUtils.setNameSuffix(subMs, subSuffix);
			initializeSubTreeNameSuffix(subMs, subSuffix, subSuffixLevel);
		}
	}

	public static void sortByMaxDistanceToLeafAndReducingTermPosition(
			List<IMonosaccharide> mss) {
		// sort descending on MaxDistanceToLeaf and ReducingTermPosition
		Collections.sort(mss, new Comparator<IMonosaccharide>() {
			public int compare(IMonosaccharide o1, IMonosaccharide o2) {
				int result = o2.getMaxDistanceToLeaf() - o1.getMaxDistanceToLeaf();
				if (result == 0) {

					result = o2.getTargetPosition(o2.getParentReducingTermPosition())
							- o1.getTargetPosition(o1.getParentReducingTermPosition());
				}
				return result;
			}
		});
	}
}
