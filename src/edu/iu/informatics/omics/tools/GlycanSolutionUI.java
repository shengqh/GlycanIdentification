package edu.iu.informatics.omics.tools;

import cn.ac.rcpa.bio.tools.solution.AbstractRcpaSolutionUI;

public class GlycanSolutionUI extends AbstractRcpaSolutionUI {
	private static final long serialVersionUID = 1L;

	private static final String title = "Glycan Software Suite";

	private static final String version = "1.0.2";

	public GlycanSolutionUI() {
		super(title, version);
	}

	@Override
	protected void addAllCommand() {
		addCommand(new GlycanStructureImageBatchBuilderUI.Command());
		addCommand(new GlycanStructureImageBuilderUI.Command());
		addCommand(new GlycanStructureToAtomCompositionBuilderUI.Command());
		addCommand(new LinearGlycanFragmentationBuilderUI.Command());
	}
	
	public static void main(String[] args) throws Exception {
		new GlycanSolutionUI().showSelf();
	}

}
