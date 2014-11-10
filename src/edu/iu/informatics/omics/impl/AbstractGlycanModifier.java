package edu.iu.informatics.omics.impl;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IGlycanModifier;
import edu.iu.informatics.omics.IMonosaccharide;


public abstract class AbstractGlycanModifier implements IGlycanModifier {

	public void modifyGlycan(Glycan glycan) {
		modifyOligosaccharide(glycan.getReducingTerm(), true);
	}

	public abstract void modifyOligosaccharide(IMonosaccharide sa, boolean recursion) ;
}
