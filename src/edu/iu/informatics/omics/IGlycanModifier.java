package edu.iu.informatics.omics;

public interface IGlycanModifier {
	void modifyGlycan(Glycan glycan);
	
	void modifyOligosaccharide(IMonosaccharide sa, boolean recursion);
}
