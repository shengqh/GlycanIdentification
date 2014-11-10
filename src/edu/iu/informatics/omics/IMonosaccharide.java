package edu.iu.informatics.omics;

import java.util.Map;

public interface IMonosaccharide extends ChemicalGroup {
	String getName();

	String getShortName();

	void setName(String name);

	void setShortName(String shortName);

	void linkToReducingTermMonosaccharide(int sourcePosition, int targetPosition,
			IMonosaccharide target);

	Map<Integer, IMonosaccharide> getMonosaccharides();
	
	Map<Integer, IMonosaccharide> getSubMonosaccharides();

	/**
	 * Find the linkage position in target monosaccharide based on linkage
	 * position in current monosaccharide
	 * 
	 * @param sourcePosition
	 * @return linkage position in target monosaccharide
	 */
	int getTargetPosition(int sourcePosition);

	/**
	 * Find the linkage position of linked monosaccharide
	 * 
	 * @param sourceMonosaccharide
	 * @return linkage position of linked monosaccharide
	 */
	int getSourcePosition(IMonosaccharide sourceMonosaccharide);

	/**
	 * Get reducing terminal oligosaccharide
	 * 
	 * @return
	 */
	IMonosaccharide getRoot();

	IMonosaccharide getParentReducingTerm();

	int getParentReducingTermPosition();

	int getDistanceToRoot();

	int getMaxDistanceToLeaf();

	Map<String, Object> getAnnotations();
	
	boolean isReducingTerm();
	
	boolean isNonreducingTerm();
}
