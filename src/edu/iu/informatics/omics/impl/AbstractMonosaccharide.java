package edu.iu.informatics.omics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.iu.informatics.omics.ChemicalGroup;
import edu.iu.informatics.omics.ChemicalGroupFactory;
import edu.iu.informatics.omics.IMonosaccharide;

public abstract class AbstractMonosaccharide extends
		AbstractMultipleChemicalGroup implements IMonosaccharide {
	public boolean isNonreducingTerm() {
		return 0 == getSubMonosaccharides().size();
	}

	public boolean isReducingTerm() {
		return null == getParentReducingTerm();
	}

	public Map<Integer, IMonosaccharide> getSubMonosaccharides() {
		Map<Integer, IMonosaccharide> result = getMonosaccharides();
		result.remove(getParentReducingTermPosition());
		return result;
	}

	protected Map<Integer, ChemicalGroup> links = new LinkedHashMap<Integer, ChemicalGroup>();

	protected List<Integer> getSortedInternalPositions() {
		List<Integer> result = new ArrayList<Integer>(getSubGroups().keySet());
		Collections.sort(result);
		return result;
	}

	private String name;

	private String shortName;

	protected ChemicalGroup core;
	
	protected IMonosaccharide parentReducingTerm;

	protected void clear() {
		getSubGroups().clear();
		getLinks().clear();
	}

	public AbstractMonosaccharide(String name, String shortName) {
		super(new ChemicalGroup[] {});
		this.name = name;
		this.shortName = shortName;
		initOligosaccharide();
	}

	public void linkToReducingTermMonosaccharide(int sourcePosition, int targetPosition,
			IMonosaccharide target) {
		linkToReducingTermOligosaccharide(sourcePosition, targetPosition,
				ChemicalGroupFactory.OH, null, target, ChemicalGroupFactory.OH,
				ChemicalGroupFactory.O);
	}

	protected void linkToReducingTermOligosaccharide(int sourcePosition, int targetPosition,
			ChemicalGroup sourceFromGroup, ChemicalGroup sourceToGroup,
			IMonosaccharide target, ChemicalGroup targetFromGroup,
			ChemicalGroup targetToGroup) {
		if (links.containsKey(sourcePosition)) {
			throw new IllegalStateException(
					"Cannot link to another oligosaccharide, there is one linked!");
		}

		if (!containSubGroup(sourcePosition, sourceFromGroup)) {
			throw new IllegalStateException("There is no " + sourceFromGroup
					+ " which is needed to connect another oligosaccharide in " + this);
		}

		if (!target.containSubGroup(targetPosition, targetFromGroup)) {
			throw new IllegalArgumentException("There is no free " + targetFromGroup
					+ " in position " + targetPosition + " of " + target.getName()
					+ " which is needed to connect another oligosaccharide");
		}

		this.replaceFirstSubGroup(sourcePosition, sourceFromGroup, sourceToGroup);
		this.setLink(sourcePosition, target);

		target.replaceFirstSubGroup(targetPosition, targetFromGroup, targetToGroup);
		target.setLink(targetPosition, this);
		
		this.parentReducingTerm = target;
	}

	private void initOligosaccharide() {
		doInitCore();
		doInitSubGroups();
		doInitLinks();
		resetAtomCounts();
	}

	public String getShortName() {
		return shortName;
	}

	public String getName() {
		return name;
	}

	abstract protected void doInitCore();

	abstract protected void doInitSubGroups();

	abstract protected void doInitLinks();

	public void setName(String name) {
		this.name = name;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public ChemicalGroup getCore() {
		return core;
	}

	@Override
	protected Map<Integer, ChemicalGroup> getModifiableLinks() {
		return links;
	}

	@Override
	public boolean hasLinks() {
		return true;
	}

	public Map<Integer, IMonosaccharide> getMonosaccharides() {
		final Map<Integer, IMonosaccharide> result = new HashMap<Integer, IMonosaccharide>();
		final Map<Integer, ChemicalGroup> cgLinks = getLinks();

		for (Integer position : cgLinks.keySet()) {
			if (cgLinks.get(position) instanceof IMonosaccharide) {
				result.put(position, (IMonosaccharide) cgLinks.get(position));
			}
		}
		return result;
	}

	public int getTargetPosition(int sourcePosition) {
		Map<Integer, IMonosaccharide> oss = getMonosaccharides();
		if (!oss.containsKey(sourcePosition)) {
			throw new IllegalArgumentException(
					"There is no oligosaccharide in position " + sourcePosition);
		}
		IMonosaccharide os = oss.get(sourcePosition);
		return os.getSourcePosition(this);
	}

	public int getSourcePosition(IMonosaccharide sourceOligosaccharide) {
		Map<Integer, IMonosaccharide> oss = getMonosaccharides();
		for (Integer position : oss.keySet()) {
			if (oss.get(position) == sourceOligosaccharide) {
				return position;
			}
		}
		throw new IllegalArgumentException(
				"There is no linkage to such oligosaccharide : "
						+ sourceOligosaccharide);
	}

	public int getDistanceToRoot() {
		int result = 1;
		IMonosaccharide curReducingTerm = this;
		while(curReducingTerm.getParentReducingTerm() != null){
			result ++;
			curReducingTerm = curReducingTerm.getParentReducingTerm();
		}
		return result;
	}

	public int getMaxDistanceToLeaf() {
		Map<Integer, IMonosaccharide> subMss = this.getSubMonosaccharides();
		int result = 1;
		for (IMonosaccharide subMs : subMss.values()) {
			int maxDistanceToLeaf = subMs.getMaxDistanceToLeaf() + 1;
			if (result < maxDistanceToLeaf) {
				result = maxDistanceToLeaf;
			}
		}

		return result;
	}

	public IMonosaccharide getParentReducingTerm(){
		return parentReducingTerm;
	}
	
	public IMonosaccharide getRoot() {
		IMonosaccharide result = this;
		while(result.getParentReducingTerm() != null){
			result = result.getParentReducingTerm();
		}
		return result;
	}

	private Map<String, Object> annotations = new HashMap<String, Object>();

	public Map<String, Object> getAnnotations() {
		return annotations;
	}
	
	@Override
	public String toString(){
		return shortName;
	}
}