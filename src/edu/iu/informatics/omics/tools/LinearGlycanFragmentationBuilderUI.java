package edu.iu.informatics.omics.tools;

import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.utils.OpenFileArgument;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class LinearGlycanFragmentationBuilderUI extends AbstractFileProcessorUI {
	private static final long serialVersionUID = -7092846791321496427L;

	private static String title = "Linear Glycan Theoretical Fragmentation Builder";
	private static String version = "1.0.0";

	private JRcpaComboBox<IsotopicType> isotopicTypeField = new JRcpaComboBox<IsotopicType>(
			"IsotopicType", "Isotopic Type", IsotopicType.values(),
			IsotopicType.Monoisotopic);

	private JRcpaComboBox<DerivativeType> derivativeTypeField = new JRcpaComboBox<DerivativeType>(
			"DerivativeType", "Derivative Type", DerivativeType.values(),
			DerivativeType.Underivatised);

	private JRcpaComboBox<AdductType> protonTypeField = new JRcpaComboBox<AdductType>(
			"AdductType", "Adduct Type", AdductType.values(), AdductType.H);

	public LinearGlycanFragmentationBuilderUI() {
		super(title, new OpenFileArgument("Glycan Structure", "inp"));
		addComponent(isotopicTypeField);
		addComponent(derivativeTypeField);
		addComponent(protonTypeField);
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new LinearGlycanFragmentationBuilder(new GlycanColFormat(), isotopicTypeField
				.getSelectedItem(), derivativeTypeField.getSelectedItem(),
				protonTypeField.getSelectedItem());
	}

	public static void main(String[] args) {
		new LinearGlycanFragmentationBuilderUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { "Annotation" };
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
