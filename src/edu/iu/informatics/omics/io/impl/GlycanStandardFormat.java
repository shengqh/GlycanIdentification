package edu.iu.informatics.omics.io.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.GlycanUtils;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.MonosaccharideFactory;
import edu.iu.informatics.omics.impl.Fucose;

public class GlycanStandardFormat extends AbstractGlycanFormat {

	private static Pattern nodePattern;

	protected static Pattern getNodePattern() {
		if (null == nodePattern) {
			nodePattern = Pattern.compile("([a-zA-Z]+(?:\\d[a-zA-Z]+){0,1})(?:([\\d])-([\\d])){0,1}(.*)$");
		}
		return nodePattern;
	}

	public void parse(Glycan glycan, String line) {
		if (StringUtils.countMatches(line, "(") != StringUtils.countMatches(line,
				")")) {
			throw new IllegalArgumentException(
					"Parse glycan from string error : the count of '(' is not equal to the count of ')' "
							+ line);
		}

		if (StringUtils.countMatches(line, "[") != StringUtils.countMatches(line,
				"]")) {
			throw new IllegalArgumentException(
					"Parse glycan from string error : the count of '[' is not equal to the count of ']' "
							+ line);
		}

		String curr = line.replace('[', '(').replace(']', ')');
		glycan
				.setReducingTerm(parseReducingTerm(curr, new ArrayList<StackItem>()).reducingTerm);
	}

	private class StackItem {
		public IMonosaccharide reducingTerm;

		public int sourcePosition;

		public int targetPosition;

		public StackItem(IMonosaccharide reducingTerm, int sourcePosition,
				int targetPosition) {
			this.reducingTerm = reducingTerm;
			this.sourcePosition = sourcePosition;
			this.targetPosition = targetPosition;
		}
	}

	private StackItem parseReducingTerm(String line, List<StackItem> stacks) {
		if (null == line || 0 == line.length()) {
			throw new IllegalArgumentException(line + " is not a valid glycan!");
		}

		if ('(' != line.charAt(0)) {
			String branchStr = GlycanUtils.getBranch(line);

			// all oligosaccharides in stack are branches of current oligosaccharide
			StackItem branch = parseLinearBranch(branchStr, stacks);

			String other = line.substring(branchStr.length());
			if (other.length() == 0) {
				return branch;
			}

			List<StackItem> branches = new ArrayList<StackItem>();
			branches.add(branch);

			return parseReducingTerm(other, branches);
		} else {
			String branchStr = GlycanUtils.getBranch(line);
			StackItem branch = parseReducingTerm(branchStr,
					new ArrayList<StackItem>());
			stacks.add(branch);
			String other = line.substring(branchStr.length() + 2);

			return parseReducingTerm(other, stacks);
		}
	}

	private StackItem parseLinearBranch(String line, List<StackItem> stacks) {
		Matcher matcher = getNodePattern().matcher(line);
		if (!matcher.find()) {
			throw new IllegalArgumentException(line + " is not a valid glycan!");
		}

		String os = matcher.group(1).trim();
		IMonosaccharide current = MonosaccharideFactory.create(os);
		int sourcePosition = 0;
		int targetPosition = 0;
		if (matcher.group(2) != null) {
			sourcePosition = Integer.parseInt(matcher.group(2));
		}
		if (matcher.group(3) != null) {
			targetPosition = Integer.parseInt(matcher.group(3));
		}

		for (StackItem item : stacks) {
			item.reducingTerm.linkToReducingTermMonosaccharide(item.sourcePosition,
					item.targetPosition, current);
		}

		StackItem currentItem = new StackItem(current, sourcePosition,
				targetPosition);

		String other = matcher.group(4).trim();

		if (other.length() == 0) {
			return currentItem;
		} else {
			ArrayList<StackItem> branches = new ArrayList<StackItem>();
			branches.add(currentItem);

			return parseLinearBranch(other, branches);
		}
	}

	public String glycanToString(Glycan glycan) {
		if (null == glycan || null == glycan.getReducingTerm()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		buildString(sb, glycan.getReducingTerm(), 0, null);
		return sb.toString();
	}

	private void buildString(StringBuilder sb, IMonosaccharide sa,
			int reducingPosition, IMonosaccharide reducingTerm) {
		int linkOutPosition = 0;
		final Map<Integer, IMonosaccharide> sas = sa.getMonosaccharides();
		for (Integer curPosition : sas.keySet()) {
			if (reducingTerm == sas.get(curPosition)) {
				linkOutPosition = curPosition;
				break;
			}
		}
		sas.remove(linkOutPosition);

		if (null != reducingTerm) {
			sb.insert(0, linkOutPosition + "-" + reducingPosition);
		}
		sb.insert(0, sa.getShortName());

		if (sas.size() > 0) {
			List<Integer> positions = new ArrayList<Integer>(sas.keySet());

			// process fucose first
			for (Integer position : positions) {
				IMonosaccharide os = sas.get(position);
				if (os instanceof Fucose) {
					if (sas.size() > 1) {
						sb.insert(0, "]");
					}
					buildString(sb, os, position, sa);
					if (sas.size() > 1) {
						sb.insert(0, "[");
					}
					positions.remove(position);
					break;
				}
			}

			if (positions.size() == 0) {
				return;
			}

			Collections.sort(positions);

			for (int i = positions.size() - 1; i >= 1; i--) {
				sb.insert(0, ")");
				buildString(sb, sas.get(positions.get(i)), positions.get(i), sa);
				sb.insert(0, "(");
			}

			buildString(sb, sas.get(positions.get(0)), positions.get(0), sa);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Glycan Standard Format";
	}
}
