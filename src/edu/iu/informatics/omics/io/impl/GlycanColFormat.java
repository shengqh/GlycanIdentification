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

public class GlycanColFormat extends AbstractGlycanFormat {
	public GlycanColFormat() {
		super();
	}

	private static Pattern nodePattern;

	protected static Pattern getNodePattern() {
		if (null == nodePattern) {
			nodePattern = Pattern
					.compile("^(?:([\\dR])-([\\d])){0,1}([a-zA-Z]+(?:\\d[a-zA-Z]+){0,1})(.*?)$");
		}
		return nodePattern;
	}

	public void parse(Glycan glycan, String line) {
		int leftBranketCount = StringUtils.countMatches(line, "(");
		int rightBranketCount = StringUtils.countMatches(line, ")");
		if (leftBranketCount != rightBranketCount) {
			throw new IllegalArgumentException(
					"Parse glycan from string error : the count of '(' is not equal to the count of ')' "
							+ line);
		}

		glycan.setReducingTerm(null);
		addToOligosacchride(glycan, null, line);
	}

	public String glycanToString(Glycan glycan) {
		if (null == glycan || null == glycan.getReducingTerm()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		buildString(sb, glycan.getReducingTerm(), 0, null);
		return sb.toString();
	}

	private void addToOligosacchride(Glycan glycan, IMonosaccharide reducingTerm,
			String line) {
		if (null == line || 0 == line.length()) {
			throw new IllegalArgumentException(line + " is not a valid glycan!");
		}

		if ('(' != line.charAt(0)) {
			Matcher matcher = getNodePattern().matcher(line);
			if (!matcher.find()) {
				throw new IllegalArgumentException(line + " is not a valid glycan!");
			}

			String nonReducing = matcher.group(4).trim();
			final IMonosaccharide current = MonosaccharideFactory.create(matcher
					.group(3));

			if (null == reducingTerm) {
				glycan.setReducingTerm(current);
			} else {
				if (null == matcher.group(1) || null == matcher.group(2)) {
					throw new IllegalArgumentException(line
							+ " is not a valid glycan, there is no linkage information!");
				}

				int targetPosition = Integer.parseInt(matcher.group(1));
				int sourcePosition = Integer.parseInt(matcher.group(2));
				current.linkToReducingTermMonosaccharide(sourcePosition,
						targetPosition, reducingTerm);
			}

			if (0 == nonReducing.length()) {
				return;
			}

			addToOligosacchride(glycan, current, nonReducing);
		} else {
			List<String> branches = GlycanUtils.getBranches(line);
			for (String branch : branches) {
				addToOligosacchride(glycan, reducingTerm, branch);
			}
		}
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
			sb.append(reducingPosition);
			sb.append('-');
			sb.append(linkOutPosition);
		}
		sb.append(sa.getShortName());

		if (sas.size() > 0) {
			if (sas.size() > 1) {
				sb.append("(");
			}
			List<Integer> positions = new ArrayList<Integer>(sas.keySet());
			Collections.sort(positions);
			boolean bFirst = true;
			for (Integer position : positions) {
				if (bFirst) {
					bFirst = false;
				} else {
					sb.append(",");
				}
				buildString(sb, sas.get(position), position, sa);
			}
			if (sas.size() > 1) {
				sb.append(")");
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Glycan COL Format";
	}
}
