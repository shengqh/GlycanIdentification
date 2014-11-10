package edu.iu.informatics.omics.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;

public class LinearGlycanFragmentationBuilder implements IFileProcessor {
	private FragmentationBuilder<Peak> fragmentationBuilder;

	private String param;

	private IGlycanReader glycanReader;

	public LinearGlycanFragmentationBuilder(IGlycanReader glycanReader,
			IsotopicType iType, DerivativeType derivativeType, AdductType protonType) {
		super();
		this.glycanReader = glycanReader;
		param = iType + "_" + derivativeType + "_" + protonType;
		IMassProxy massProxy = MassProxyFactory.getMassProxy(iType, derivativeType,
				protonType);
		fragmentationBuilder = new LinearFragmentationBuilderImpl(massProxy);
	}

	public List<String> process(String originFile) throws Exception {
		Glycan glycan = glycanReader.read(originFile);

		PeakList<Peak> list = fragmentationBuilder.build(glycan);

		String resultFile = originFile + "_" + param + ".fragmentation";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			pw.println(list.toString());
			pw.println();
			pw.println(fragmentationBuilder.toString());
		} finally {
			pw.close();
		}
		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		File[] files = new File("data/Permethylated")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith("inp");
					}
				});

		IGlycanReader glycanReader = new GlycanColFormat();
		LinearGlycanFragmentationBuilder builder = new LinearGlycanFragmentationBuilder(
				glycanReader, IsotopicType.Average, DerivativeType.Permethylated,
				AdductType.Na);
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			builder.process(file.getAbsolutePath());
		}

		// builder = new LinearGlycanFragmentationBuilder(true,
		// DerivativeType.Underivatised, ProtonType.Na);
		// for (File file : files) {
		// System.out.println(file.getAbsolutePath());
		// builder.process(file.getAbsolutePath());
		// }
	}
}
