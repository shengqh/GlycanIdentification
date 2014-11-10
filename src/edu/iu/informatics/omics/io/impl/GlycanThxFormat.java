package edu.iu.informatics.omics.io.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import edu.iu.informatics.omics.io.IGlycanReader;

public class GlycanThxFormat implements IGlycanReader {
	private static Pattern nodePattern;

	protected static Pattern getNodePattern() {
		if (null == nodePattern) {
			nodePattern = Pattern.compile("(\\d{0,1})([^\\(]+)");
		}
		return nodePattern;
	}

	public GlycanThxFormat() {
		super();
	}

	public Glycan read(String filename) throws IOException {
		Glycan result = new Glycan();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {
			String line = br.readLine();
			if (line == null) {
				throw new IllegalArgumentException(filename + " contains nothing!");
			}

			result.setName(line.substring(1).trim());

			line = br.readLine();

			parse(result, line);
		} finally {
			br.close();
		}

		return result;
	}

	public Glycan parse(String line) {
		Glycan result = new Glycan();
		parse(result, line);
		return result;
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

	protected void addToOligosacchride(Glycan glycan,
			IMonosaccharide reducingTerm2, String line) {
		if (null == line || 0 == line.length()) {
			throw new IllegalArgumentException(line + " is not a valid glycan!");
		}

		Matcher matcher = getNodePattern().matcher(line);
		if (!matcher.find()) {
			throw new IllegalArgumentException(line + " is not a valid glycan!");
		}
		final IMonosaccharide current = MonosaccharideFactory.create(matcher
				.group(2));
		if (null == reducingTerm2) {
			glycan.setReducingTerm(current);
		} else {
			if (null == matcher.group(1)) {
				throw new IllegalArgumentException(line
						+ " is not a valid glycan, there is no linkage information!");
			}
			int connectType = Integer.parseInt(matcher.group(1));
			current.linkToReducingTermMonosaccharide(1, connectType, reducingTerm2);
		}

		if (!GlycanUtils.hasSubTree(line)) {
			return;
		}

		List<String> branches = GlycanUtils.getBranches(line);
		for (String branch : branches) {
			addToOligosacchride(glycan, current, branch);
		}
	}

	private void buildString(StringBuilder sb, IMonosaccharide sa) {
		sb.append(sa.getShortName());
		final Map<Integer, IMonosaccharide> sas = sa.getMonosaccharides();
		sas.remove(1);
		if (sas.size() > 0) {
			sb.append("(");
			List<Integer> positions = new ArrayList<Integer>(sas.keySet());
			Collections.sort(positions);
			boolean bFirst = true;
			for (Integer position : positions) {
				if (bFirst) {
					bFirst = false;
				} else {
					sb.append(",");
				}
				sb.append(position);
				buildString(sb, sas.get(position));
			}
			sb.append(")");
		}
	}

	public String glycanToString(Glycan glycan) {
		StringBuilder sb = new StringBuilder();
		buildString(sb, glycan.getReducingTerm());
		return sb.toString();
	}
}
