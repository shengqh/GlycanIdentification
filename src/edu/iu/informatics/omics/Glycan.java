package edu.iu.informatics.omics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.chem.AtomCompositionUtils;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class Glycan {
	public Glycan(String name, IGlycanParser parser, String structure) {
		super();
		if (parser != null) {
			this.parser = parser;
		} else {
			this.parser = new GlycanColFormat();
		}
		this.parser.parse(this, structure);
	}

	public Glycan(String name, String structure) {
		super();
		this.parser = new GlycanColFormat();
		this.parser.parse(this, structure);
	}

	public Glycan() {
		super();
		this.parser = new GlycanColFormat();
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private IMonosaccharide reducingTerm;

	public IMonosaccharide getReducingTerm() {
		return reducingTerm;
	}

	public void setReducingTerm(IMonosaccharide reducingTerm) {
		this.reducingTerm = reducingTerm;
	}

	private IGlycanParser parser;

	public IGlycanParser getParser() {
		return parser;
	}

	public void setParser(IGlycanParser parser) {
		if (parser != null) {
			this.parser = parser;
		} else {
			this.parser = new GlycanColFormat();
		}
	}

	@Override
	public String toString() {
		return parser.glycanToString(this);
	}

	public boolean isLinear() {
		Map<Integer, IMonosaccharide> rootLinks = reducingTerm.getMonosaccharides();
		if (rootLinks.size() > 1) {
			return false;
		}

		IMonosaccharide current = rootLinks.values().iterator().next();
		int sourcePosition = current.getSourcePosition(reducingTerm);
		while (true) {
			Map<Integer, IMonosaccharide> links = current.getMonosaccharides();

			// remove the link with reducing term
			links.remove(sourcePosition);

			if (links.size() > 1) {
				return false;
			}

			if (links.size() == 0) {
				break;
			}

			IMonosaccharide next = links.values().iterator().next();
			if (next == null) {
				break;
			}

			sourcePosition = next.getSourcePosition(current);
			current = next;
		}

		return true;
	}

	public List<IMonosaccharide> getOligosaccharides() {
		List<IMonosaccharide> result = new ArrayList<IMonosaccharide>();
		List<IMonosaccharide> popup = new ArrayList<IMonosaccharide>();
		popup.add(this.reducingTerm);

		while (popup.size() > 0) {
			IMonosaccharide os = popup.get(0);
			popup.remove(0);
			result.add(os);

			Map<Integer, IMonosaccharide> oss = os.getMonosaccharides();
			oss.remove(os.getParentReducingTermPosition());
			popup.addAll(oss.values());
		}

		return result;
	}

	public List<IMonosaccharide> getOligosaccharidesFromNonreducingTerm() {
		if (!isLinear()) {
			throw new IllegalStateException("Current glycan is not linear");
		}
		ArrayList<IMonosaccharide> result = new ArrayList<IMonosaccharide>();

		IMonosaccharide curr = reducingTerm;
		result.add(0, reducingTerm);
		do {
			Map<Integer, IMonosaccharide> subMss = curr.getSubMonosaccharides();
			if (subMss.size() > 0) {
				curr = subMss.values().iterator().next();
				result.add(0, curr);
			} else {
				break;
			}
		} while (true);

		return result;
	}

	public List<IMonosaccharide> getOligosaccharidesFromReducingTerm() {
		ArrayList<IMonosaccharide> result = new ArrayList<IMonosaccharide>();
		addMonosaccharide(result, reducingTerm);
		return result;
	}

	private void addMonosaccharide(List<IMonosaccharide> result,
			IMonosaccharide curr) {
		result.add(curr);
		Map<Integer, IMonosaccharide> subMss = curr.getSubMonosaccharides();
		ArrayList<IMonosaccharide> mss = new ArrayList<IMonosaccharide>(subMss
				.values());
		OligosaccharideUtils.sortByMaxDistanceToLeafAndReducingTermPosition(mss);
		for (IMonosaccharide ms : mss) {
			addMonosaccharide(result, ms);
		}
	}

	public Map<IMonosaccharide, IMonosaccharide> getChildrenParentMap() {
		Map<IMonosaccharide, IMonosaccharide> result = new HashMap<IMonosaccharide, IMonosaccharide>();
		addToMap(result, getReducingTerm(), null);
		return result;
	}

	private void addToMap(Map<IMonosaccharide, IMonosaccharide> result,
			IMonosaccharide currOs, IMonosaccharide currReducingTerm) {
		if (currReducingTerm != null) {
			result.put(currOs, currReducingTerm);
		}

		Map<Integer, IMonosaccharide> oss = currOs.getMonosaccharides();
		List<IMonosaccharide> nonReducingTerms = new ArrayList<IMonosaccharide>(oss
				.values());
		nonReducingTerms.remove(currReducingTerm);

		for (IMonosaccharide os : nonReducingTerms) {
			result.put(os, currOs);
			addToMap(result, os, currOs);
		}
	}

	public Map<Character, Integer> getAtomCounts() {
		Map<Character, Integer> result = new HashMap<Character, Integer>();
		List<IMonosaccharide> oos = this.getOligosaccharides();
		for (IMonosaccharide os : oos) {
			Map<Character, Integer> ac = os.getAtomCounts();
			for (Character c : ac.keySet()) {
				if (result.containsKey(c)) {
					result.put(c, result.get(c) + ac.get(c));
				} else {
					result.put(c, ac.get(c));
				}
			}
		}
		return result;
	}

	public String getAtomCompositionStr() {
		Map<Character, Integer> acs = getAtomCounts();
		return AtomCompositionUtils.mapToString(acs);
	}

	public Map<String, Integer> getMonosaccharideCount() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		List<IMonosaccharide> oos = this.getOligosaccharides();
		for (IMonosaccharide os : oos) {
			String shortName = os.getShortName();
			if (result.containsKey(shortName)) {
				result.put(shortName, result.get(shortName) + 1);
			} else {
				result.put(shortName, 1);
			}
		}
		return result;
	}

	public String getMonosaccharideCompositionStr() {
		Map<String, Integer> msMap = this.getMonosaccharideCount();
		List<String> names = new ArrayList<String>(msMap.keySet());
		Collections.sort(names);
		StringBuilder sb = new StringBuilder();
		for (String shortName : names) {
			sb.append(shortName + "(" + msMap.get(shortName) + ")");
		}
		return sb.toString();
	}
}
