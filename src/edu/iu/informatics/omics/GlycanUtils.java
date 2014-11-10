package edu.iu.informatics.omics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.Pair;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.IGlycanReader;

public final class GlycanUtils {
	private static Pattern subTreePattern;

	protected static Pattern getSubTreePattern() {
		if (null == subTreePattern) {
			subTreePattern = Pattern.compile("\\((.+)\\)$");
		}
		return subTreePattern;
	}

	public static boolean hasSubTree(String formula) {
		Matcher matcher = getSubTreePattern().matcher(formula);
		return matcher.find();
	}

	/**
	 * Get branches based on formula such like 6Man(4Man,6Gal,3Glc) or
	 * 4Man,6Gal,3Glc
	 * 
	 * @param line :
	 *          the formula including reducing terminal and brankets or just
	 *          branch formula
	 * @return
	 */
	public static List<String> getBranches(String formula) {
		if (null == formula || 0 == formula.length()) {
			throw new IllegalArgumentException("Glycan formula is empty!");
		}

		String line;
		Matcher matcher = getSubTreePattern().matcher(formula);
		if (!matcher.find()) {
			line = formula;
		} else {
			line = matcher.group(1);
		}

		List<String> result = new ArrayList<String>();
		StringBuilder sbLeft = new StringBuilder();
		int leftBracket = 0;
		int rightBracket = 0;
		for (int i = 0; i < line.length(); i++) {
			if (',' == line.charAt(i)) {
				if (leftBracket == rightBracket) {
					result.add(sbLeft.toString().trim());
					sbLeft = new StringBuilder();
					continue;
				}
			} else if ('(' == line.charAt(i)) {
				leftBracket++;
			} else if (')' == line.charAt(i)) {
				rightBracket++;
			}

			sbLeft.append(line.charAt(i));
		}

		String right = sbLeft.toString().trim();
		if (right.length() > 0) {
			result.add(right);
		}

		return result;
	}

	// Get branch between '(' and ')'
	public static String getBranch(String formula) {
		if (null == formula || 0 == formula.length()) {
			throw new IllegalArgumentException("Glycan formula is empty!");
		}

		String line = formula;

		StringBuilder sb = new StringBuilder();
		int leftBracket = 0;
		int rightBracket = 0;
		for (int i = 0; i < line.length(); i++) {
			if ('(' == line.charAt(i)) {
				leftBracket++;
				if (leftBracket == 1) {
					if (sb.length() != 0) {
						break;
					} else {
						continue;
					}
				}
			} else if (')' == line.charAt(i)) {
				rightBracket++;
				if (rightBracket == leftBracket) {
					break;
				}
			}

			sb.append(line.charAt(i));
		}

		return sb.toString();
	}

	public static String convertToLinkage(String glycanStr, int newLinkage) {
		String newLinkageStr = String.valueOf(newLinkage) + "$1";
		return glycanStr.replaceAll("[2346](-\\d)", newLinkageStr);
	}

	public static Glycan readAs(IGlycanReader reader, String filename, int linkage)
			throws IOException {
		Glycan old = reader.read(filename);
		String glycanStr = reader.glycanToString(old);
		return parseAs(reader, glycanStr, linkage);
	}

	public static Glycan parseAs(IGlycanParser parser, String formula, int linkage) {
		String result = formula.replaceAll("[2346](-\\d)", linkage + "$1");
		return parser.parse(result);
	}

	public static List<Pair<FragmentationType, Peak>> convertFromMap(
			Map<FragmentationType, Peak> virtualPeaks) {
		ArrayList<Pair<FragmentationType, Peak>> result = new ArrayList<Pair<FragmentationType, Peak>>();
		for (FragmentationType key : virtualPeaks.keySet()) {
			result.add(new Pair<FragmentationType, Peak>(key, virtualPeaks.get(key)));
		}
		return result;
	}

	private static double getMaxIntensity(Peak peak, PeakList<Peak> originPeaks,
			double threshold) {
		double result = 0.0;
		for (Peak originPeak : originPeaks.getPeaks()) {
			if (Math.abs(peak.getMz() - originPeak.getMz()) < threshold) {
				if (result < originPeak.getIntensity()) {
					result = originPeak.getIntensity();
				}
			}
		}

		return result;
	}

	private static boolean isKeptIonTypes(Pair<FragmentationType, Peak> pair,
			List<FragmentationType> keptIonTypes) {
		if (0.0 == pair.snd.getMz()) {
			return false;
		}

		return null == keptIonTypes || keptIonTypes.contains(pair.fst);
	}

	public static Map<FragmentationType, Integer> getOrderMap(
			List<FragmentationType> removedIonTypes,
			List<FragmentationType> keptIonTypes,
			Map<FragmentationType, Peak> virtualPeaks, PeakList<Peak> originPeaks,
			double threshold) {

		for (FragmentationType fType : removedIonTypes) {
			virtualPeaks.remove(fType);
		}

		List<Pair<FragmentationType, Peak>> peaks = convertFromMap(virtualPeaks);
		for (Pair<FragmentationType, Peak> pair : peaks) {
			double intensity = getMaxIntensity(pair.snd, originPeaks, threshold);
			pair.snd.setIntensity(intensity);
		}
		Collections.sort(peaks, new Comparator<Pair<FragmentationType, Peak>>() {
			public int compare(Pair<FragmentationType, Peak> o1,
					Pair<FragmentationType, Peak> o2) {
				return Double.compare(o2.snd.getIntensity(), o1.snd.getIntensity());
			}
		});

		for (Pair<FragmentationType, Peak> p : peaks) {
			if (p.snd.getIntensity() == 0) {
				break;
			}
//			System.out.println("\t\t" + p.snd);
		}
//		System.out.println();

		Map<FragmentationType, Integer> orders = new HashMap<FragmentationType, Integer>();
		int curOrder = 1;
		for (int j = 0; j < peaks.size(); j++) {
			Peak curPeak = peaks.get(j).snd;
			if (j > 0) {
				Peak lastPeak = peaks.get(j - 1).snd;
				if (curPeak.getIntensity() != lastPeak.getIntensity()) {
					curOrder = j + 1;
				}
			}

			if (isKeptIonTypes(peaks.get(j), keptIonTypes)) {
				orders.put(peaks.get(j).fst, curOrder);
			}
		}

		return orders;
	}
}
