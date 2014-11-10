package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.GlycanUtils;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class PermethylatedCrossRingIonTypeComparator {
	private static IGlycanReader glycanReader = new GlycanColFormat();
	
	private static final double THRESHOLD = 0.3;

	private static String DEXTRAN = "Dextran_glc";

	private static String MALTOOLIGOSACCHARIDES = "Maltooligosaccharides_glc";

	private static FragmentationType[] ionTypes = new FragmentationType[] {
			FragmentationType.A04, FragmentationType.X04, FragmentationType.A14,
			FragmentationType.X14 };

	public static void main(String[] args) throws Exception {
		runPermethylatedSamples();
	}

	private static void runSample(String dextranType,
			String maltooligsaccharidesType, IsotopicType iType,
			DerivativeType dType, File data) throws Exception {
		IMassProxy massProxy = MassProxyFactory.getMassProxy(iType, dType,
				AdductType.Na);

		FragmentationBuilder<Peak> builder = new LinearFragmentationBuilderImpl(
				massProxy);

		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();

		find(dextranType, dType, builder, reader, data);
		find(maltooligsaccharidesType, dType, builder, reader, data);
	}

	private static void runPermethylatedSamples() throws Exception {
		runSample("Permethylated_" + DEXTRAN, "Permethylated_"
				+ MALTOOLIGOSACCHARIDES, IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, new File("data/Permethylated"));
	}

	private static void find(String nameType, DerivativeType dType,
			FragmentationBuilder<Peak> builder, PeakListReader<Peak> reader,
			final File data) throws Exception, FileNotFoundException {
		List<File> files = new ArrayList<File>();
		for (int i = 5; i <= 10; i++) {
			files.add(new File(data.getAbsolutePath(), nameType + i + ".inp"));
		}

		PrintWriter pw = new PrintWriter(new File(data, data.getName() + nameType
				+ "_iontypes.xls"));
		try {
			pw.print("\t");
			for (FragmentationType aType : ionTypes) {
				pw.print("\t" + aType);
			}
			pw.println();

			for (File file : files) {
				final String originPeakFile = RcpaFileUtils.changeExtension(file
						.getAbsolutePath(), "txt.ann");
				final PeakList<Peak> originPeaks = reader.read(originPeakFile);

				final Glycan glycan16 = GlycanUtils.readAs(glycanReader, file.getAbsolutePath(), 6);
				final Glycan glycan14 = GlycanUtils.readAs(glycanReader, file.getAbsolutePath(), 4);

				final List<IMonosaccharide> oss16 = glycan16
						.getOligosaccharidesFromNonreducingTerm();
				final List<IMonosaccharide> oss14 = glycan14
						.getOligosaccharidesFromNonreducingTerm();

				for (int i = 1; i < oss16.size() - 1; i++) {
					pw.print(file.getName() + "\toligo" + (i + 1));
					Map<FragmentationType, Peak> peak16Map = builder.buildMap(oss16
							.get(i));
					Map<FragmentationType, Peak> peak14Map = builder.buildMap(oss14
							.get(i));
					for (FragmentationType aType : ionTypes) {
						Peak expectPeak = peak16Map.get(aType);
						if (expectPeak.getMz() == 0.0) {
							expectPeak = peak14Map.get(aType);
						}

						Peak realPeak = getMaxIntensity(expectPeak, originPeaks);
						pw.print("\t" + realPeak.getIntensity());
					}
					pw.println();
				}
			}
		} finally {
			pw.close();
		}
	}

	private static Peak getMaxIntensity(Peak peak, PeakList<Peak> originPeaks) {
		Peak result = new Peak(0.0, 0.0);
		for (Peak originPeak : originPeaks.getPeaks()) {
			if (Math.abs(peak.getMz() - originPeak.getMz()) < THRESHOLD) {
				if (result.getIntensity() < originPeak.getIntensity()) {
					result = originPeak;
				}
			}
		}

		return result;
	}

}
