package edu.iu.informatics.omics.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.Fucose;

public class OligosaccharideImageLevelBuilder {
	public static final String LEVEL = "DRAW_LEVEL";

	public static final String DEPTH = "DRAW_DEPTH";

	public static final String FUCOSE_DIRECTION = "FUCOSE_DIRECTION";

	public static double getDirection(IMonosaccharide os) {
		return (Double) os.getAnnotations().get(FUCOSE_DIRECTION);
	}

	public static void setDirection(IMonosaccharide os, double value) {
		os.getAnnotations().put(FUCOSE_DIRECTION, value);
	}

	private static Comparator<Double> decendDoubleOrder = new Comparator<Double>() {
		public int compare(Double o1, Double o2) {
			if (o2 > o1) {
				return -1;
			} else if (o2 < o1) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private static Comparator<Integer> decendIntegerOrder = new Comparator<Integer>() {
		public int compare(Integer o1, Integer o2) {
			return o2 - o1;
		}
	};

	public static void assignDrawDepth(Glycan glycan) {
		assignDrawDepth(glycan.getReducingTerm(), 1);
	}

	private static void assignDrawDepth(IMonosaccharide os, int depth) {
		setDepth(os, depth);
		Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		oss.remove(os.getParentReducingTermPosition());

		for (IMonosaccharide subOs : oss.values()) {
			if (subOs instanceof Fucose) {
				setDepth(subOs, depth);
			} else {
				assignDrawDepth(subOs, depth + 1);
			}
		}
	}

	/**
	 * Assign the draw level of oligosaccharide based on branched glycan structure
	 * 
	 * @param glycan
	 */
	public static void assignDrawLevel(Glycan glycan) {
		IMonosaccharide root = glycan.getReducingTerm();

		assignDrawDepth(glycan);

		List<IMonosaccharide> leaves = new ArrayList<IMonosaccharide>();

		initializeLevel(leaves, null, 0, root);

		determinateFucoseDirection(glycan);

		while (true) {
			solveFucoseConflict(glycan.getOligosaccharides());

			double minLevel = getMinLevel(root);
			if (minLevel == 1) {
				break;
			}

			addLevel(root, -minLevel + 1);

			boolean modified = true;
			while (modified) {
				modified = false;
				for (int i = 1; i < leaves.size(); i++) {
					double curLevel = getLevel(leaves.get(i));
					if (curLevel == getLevel(leaves.get(i - 1))) {
						modified = true;
						for (int j = i; j < leaves.size(); j++) {
							setLevel(leaves.get(j), getLevel(leaves.get(j)) + 1);
						}
					}
				}
			}

			updateLevel(leaves, root, null);
		}
	}

	public static void determinateFucoseDirection(Glycan glycan) {
		Map<IMonosaccharide, IMonosaccharide> childParentMap = glycan
				.getChildrenParentMap();

		List<IMonosaccharide> oligosaccharides = glycan.getOligosaccharides();

		Map<Integer, List<IMonosaccharide>> depthOsListMap = new LinkedHashMap<Integer, List<IMonosaccharide>>();
		for (IMonosaccharide os : oligosaccharides) {
			int depth = getDepth(os);
			if (!depthOsListMap.containsKey(depth)) {
				depthOsListMap.put(depth, new ArrayList<IMonosaccharide>());
			}
			depthOsListMap.get(depth).add(os);
		}

		for (List<IMonosaccharide> oss : depthOsListMap.values()) {
			double maxLevel = -Double.MAX_VALUE;
			double minLevel = Double.MAX_VALUE;
			for (IMonosaccharide os : oss) {
				if (os instanceof Fucose) {
					continue;
				}

				double osLevel = getLevel(os);
				if (maxLevel < osLevel) {
					maxLevel = osLevel;
				}
				if (minLevel > osLevel) {
					minLevel = osLevel;
				}
			}

			for (IMonosaccharide os : oss) {
				if (!(os instanceof Fucose)) {
					continue;
				}

				IMonosaccharide parentOs = childParentMap.get(os);
				double osLevel = getLevel(parentOs);

				if (maxLevel == osLevel) {
					setDirection(os, 1);
				} else {
					setDirection(os, -1);
				}

				if (minLevel == osLevel) {
					setDirection(os, -1);
				}

				setLevel(os, osLevel + getDirection(os));
			}
		}
	}

	private static double getMinLevel(IMonosaccharide os) {
		double result = getLevel(os);

		Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		oss.remove(os.getParentReducingTermPosition());

		for (IMonosaccharide subOs : oss.values()) {
			double osLevel = getMinLevel(subOs);

			if (result > osLevel) {
				result = osLevel;
			}
		}

		return result;
	}

	private static void solveFucoseConflict(List<IMonosaccharide> osList) {
		for (IMonosaccharide subOs : osList) {
			if (subOs instanceof Fucose) {
				double fucLevel = getLevel(subOs);
				int fucDepth = getDepth(subOs);

				IMonosaccharide conflictOs = findNotFucoseOligosaccharide(osList,
						fucLevel, fucDepth, subOs);

				if (conflictOs != null) {
					addLevel(conflictOs, getDirection(subOs));
				}
			}
		}
	}

	private static void addLevel(IMonosaccharide os, double level) {
		setLevel(os, getLevel(os) + level);

		Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		oss.remove(os.getParentReducingTermPosition());

		for (IMonosaccharide subOs : oss.values()) {
			addLevel(subOs, level);
		}
	}

	private static IMonosaccharide findNotFucoseOligosaccharide(
			List<IMonosaccharide> osList, double fucLevel, int fucDepth,
			IMonosaccharide currentFucose) {
		for (IMonosaccharide os : osList) {
			if (os == currentFucose) {
				continue;
			}

			if (os instanceof Fucose) {
				continue;
			}

			if (getLevel(os) == fucLevel && getDepth(os) == fucDepth) {
				return os;
			}
		}

		return null;
	}

	public static double getLevel(IMonosaccharide os) {
		return (Double) (os.getAnnotations().get(LEVEL));
	}

	public static void setLevel(IMonosaccharide os, double level) {
		os.getAnnotations().put(LEVEL, level);
	}

	public static int getDepth(IMonosaccharide os) {
		return (Integer) (os.getAnnotations().get(DEPTH));
	}

	public static void setDepth(IMonosaccharide os, int value) {
		os.getAnnotations().put(DEPTH, value);
	}

	private static void updateLevel(List<IMonosaccharide> leaves,
			IMonosaccharide os, IMonosaccharide reducingTerm) {
		if (leaves.contains(os)) {
			return;
		}

		Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
		if (reducingTerm != null) {
			oss.remove(os.getSourcePosition(reducingTerm));
		}

		IMonosaccharide fucose = null;
		List<Double> levels = new ArrayList<Double>();
		for (IMonosaccharide child : oss.values()) {
			if (child instanceof Fucose) {
				fucose = child;
				continue;
			}
			updateLevel(leaves, child, os);
			levels.add(getLevel(child));
		}

		if (levels.size() == 0) {
			// it only contain a fucose as leaf
			throw new IllegalStateException(
					"Current oligosaccharide should be in the leaves list!");
		}

		if (levels.size() == 1) {
			setLevel(os, levels.get(0));
		} else {
			Collections.sort(levels, decendDoubleOrder);

			if (levels.size() == 3) {
				setLevel(os, levels.get(1));
			} else {
				Double sum = 0.0;
				for (Double level : levels) {
					sum += level;
				}
				setLevel(os, sum / levels.size());
			}
		}
		if (fucose != null) {
			setLevel(fucose, getLevel(os) + getDirection(fucose));
		}
	}

	public static double getMaxLevel(Glycan glycan) {
		double result = 0;
		List<IMonosaccharide> oss = glycan.getOligosaccharides();
		for (IMonosaccharide os : oss) {
			double level = getLevel(os);
			if (level > result) {
				result = level;
			}
		}
		return result;
	}

	public static void initializeLevel(List<IMonosaccharide> leaf,
			IMonosaccharide reducingTerm, double currentLevel,
			IMonosaccharide currentOs) {
		setLevel(currentOs, currentLevel - 1);
		Map<Integer, IMonosaccharide> oss = currentOs.getMonosaccharides();
		if (reducingTerm != null) {
			oss.remove(currentOs.getSourcePosition(reducingTerm));
		}

		for (Integer position : oss.keySet()) {
			IMonosaccharide os = oss.get(position);
			if (os instanceof Fucose) {
				setLevel(os, currentLevel - 1);
				oss.remove(position);
				break;
			}
		}

		if (0 == oss.size()) {
			leaf.add(currentOs);
			return;
		}

		if (1 == oss.size()) {
			initializeLevel(leaf, currentOs, currentLevel, oss.values().iterator()
					.next());
		} else {
			List<Integer> links = new ArrayList<Integer>(oss.keySet());

			Collections.sort(links, decendIntegerOrder);

			if (2 == links.size()) {
				initializeLevel(leaf, currentOs, currentLevel - 1, oss
						.get(links.get(0)));
				initializeLevel(leaf, currentOs, currentLevel + 1, oss
						.get(links.get(1)));
			} else {
				for (int i = 0; i < links.size(); i++) {
					initializeLevel(leaf, currentOs, currentLevel + i - 1, oss.get(links
							.get(i)));
				}
			}
		}
	}
}
