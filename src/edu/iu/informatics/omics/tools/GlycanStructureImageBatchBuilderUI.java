package edu.iu.informatics.omics.tools;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorWithFileArgumentUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaDoubleField;
import cn.ac.rcpa.utils.DirectoryArgument;
import cn.ac.rcpa.utils.OpenFileArgument;
import edu.iu.informatics.omics.image.GlycanImageLeaf2RootBuilder;
import edu.iu.informatics.omics.image.GlycanImageRoot2LeafBuilder;
import edu.iu.informatics.omics.image.IGlycanImageBuilder;
import edu.iu.informatics.omics.io.IGlycanParser;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.GlycanStandardFormat;

public class GlycanStructureImageBatchBuilderUI extends
		AbstractFileProcessorWithFileArgumentUI {
	private static final long serialVersionUID = -3002276212774963304L;

	private static String title = "Glycan Structure Image Batch Builder";

	private static String version = "1.0.1";

	private JRcpaComboBox<IGlycanImageBuilder> cbImageBuilder;

	private static JRcpaDoubleField txtWidth = new JRcpaDoubleField("width",
			"Monosaccharide Size", 40, true);

	private JRcpaComboBox<IGlycanParser> cbGlycanParser;

	public GlycanStructureImageBatchBuilderUI() {
		super(Constants.getSQHTitle(title, version), new DirectoryArgument(
				"Save Image"), new OpenFileArgument("Glycan Structure", "txt"));

		IGlycanImageBuilder r2lBuilder = new GlycanImageRoot2LeafBuilder();
		IGlycanImageBuilder l2rBuilder = new GlycanImageLeaf2RootBuilder();

		this.cbImageBuilder = new JRcpaComboBox<IGlycanImageBuilder>(
				"GlycamImageBuilder", "Glycan Image Builder",
				new IGlycanImageBuilder[] { r2lBuilder, l2rBuilder }, l2rBuilder);
		this.addComponent(cbImageBuilder);

		IGlycanParser colParser = new GlycanColFormat();
		IGlycanParser standardParser = new GlycanStandardFormat();

		this.cbGlycanParser = new JRcpaComboBox<IGlycanParser>("GlycanParser",
				"Glycan Parser", new IGlycanParser[] { colParser, standardParser },
				colParser);

		this.addComponent(txtWidth);
		this.addComponent(cbGlycanParser);
	}

	@Override
	protected IFileProcessor getProcessor() throws Exception {
		return new GlycanStructureImageBatchBuilder(cbImageBuilder
				.getSelectedItem(), cbGlycanParser.getSelectedItem(), (int) txtWidth
				.getValue(), getArgument());
	}

	public static void main(String[] args) {
		new GlycanStructureImageBatchBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { "Image" };
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
