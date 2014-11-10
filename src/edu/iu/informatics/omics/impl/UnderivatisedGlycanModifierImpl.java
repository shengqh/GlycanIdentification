package edu.iu.informatics.omics.impl;

import edu.iu.informatics.omics.IMonosaccharide;

public class UnderivatisedGlycanModifierImpl extends AbstractGlycanModifier {
	@Override
	public String toString() {
		return "Underivatised";
	}

	@Override
	public void modifyOligosaccharide(IMonosaccharide sa, boolean recursion) {
	}
}
