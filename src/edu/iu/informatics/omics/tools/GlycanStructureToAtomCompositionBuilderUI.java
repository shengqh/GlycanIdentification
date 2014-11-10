package edu.iu.informatics.omics.tools;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.utils.OpenFileArgument;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.GlycanStandardFormat;

public class GlycanStructureToAtomCompositionBuilderUI extends
		AbstractFileProcessorUI {
	private static final long serialVersionUID = -6700650997418688064L;

	private static String title = "Glycan Structure To Atom Composition Builder";

	private static String version = "1.0.1";

	private JRcpaComboBox<IGlycanParser> cbGlycanParser;

	public GlycanStructureToAtomCompositionBuilderUI() {
		super(Constants.getSQHTitle(title, version), new OpenFileArgument(
				"Glycan Structure", "txt"));

		IGlycanParser colParser = new GlycanColFormat();
		IGlycanParser standardParser = new GlycanStandardFormat();

		this.cbGlycanParser = new JRcpaComboBox<IGlycanParser>("GlycanParser",
				"Glycan Parser", new IGlycanParser[] { colParser, standardParser },
				colParser);

		this.addComponent(cbGlycanParser);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new GlycanStructureToAtomCompositionBuilder(cbGlycanParser
				.getSelectedItem());
	}

	public static void main(String[] args) {
		new GlycanStructureToAtomCompositionBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { "Profile" };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}
}
