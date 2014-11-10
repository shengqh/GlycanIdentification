package edu.iu.informatics.omics.analysis;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
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

/**
 * Annotate the spectrum using each oligosaccharide of the sample as 14 and as
 * 16 connection
 * 
 * @author sheng
 * 
 */
public class SpectrumAs14As16Annotator implements IFileProcessor {

	private static final double THRESHOLD = 0.3;

	private IMassProxy massProxy;

	private FragmentationBuilder<Peak> builder;

	private PeakListReader<Peak> reader;
	
	private IGlycanReader glycanReader;

	public SpectrumAs14As16Annotator(IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType,
			AdductType pType, PeakListReader<Peak> reader) {
		super();
		this.glycanReader = glycanReader;
		this.reader = reader;
		massProxy = MassProxyFactory.getMassProxy(iType, dType, pType);
		builder = new LinearFragmentationBuilderImpl(massProxy);
	}

	public static void main(String[] args) throws Exception {
		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();
		
		IGlycanReader glycanReader = new GlycanColFormat();
		//
		// new SpectrumAs14As16Annotator(IsotopicType.Average,
		// DerivativeType.Underivatised, AdductType.Na, reader)
		// .process("data/Underivatised/Dextran_glc09.inp");
		// new SpectrumAs14As16Annotator(IsotopicType.Average,
		// DerivativeType.Underivatised, AdductType.Na, reader)
		// .process("data/Underivatised/Maltooligosaccharides_glc10.inp");

		String[] suffixes = new String[] { "06", "07", "08", "09", "10" };
		for (String suffix : suffixes) {
			new SpectrumAs14As16Annotator(glycanReader, IsotopicType.Monoisotopic,
					DerivativeType.Permethylated, AdductType.Na, reader)
					.process("data/NewPermethylated/Permethylated_Dextran_glc" + suffix
							+ ".inp");
			new SpectrumAs14As16Annotator(glycanReader, IsotopicType.Monoisotopic,
					DerivativeType.Permethylated, AdductType.Na, reader)
					.process("data/NewPermethylated/Permethylated_Maltooligosaccharides_glc"
							+ suffix + ".inp");
		}
	}

	public List<String> process(String originSeqFile) throws Exception {
		String originPeakFile = RcpaFileUtils.changeExtension(originSeqFile,
				"txt.ann");
		PeakList<Peak> peaks14 = getAnnotatedPeakList(originSeqFile,
				originPeakFile, 4);
		PeakList<Peak> peaks16 = getAnnotatedPeakList(originSeqFile,
				originPeakFile, 6);

		String resultPeakFile = RcpaFileUtils.changeExtension(originSeqFile,
				"txt.1416.annotation");
		PrintWriter pw = new PrintWriter(resultPeakFile);
		DecimalFormat df2 = new DecimalFormat("0.00");
		try {
			pw.println("#" + originSeqFile);
			pw.println("#The number in bracket means the rank of that ion in all ion types of that oligosaccharide except B/Y/C/Z based on intensity, rank 1 peak with highest intensity");
			pw.println("#mz\tintensity\tAs14\tAs16");
			pw.println("Precursor\t" + peaks14.getPrecursor());
			double maxPeakIntensity = peaks14.getMaxIntensity();
			for (int i = 0; i < peaks14.getPeaks().size(); i++) {
				StringBuilder sb = new StringBuilder();

				Peak peak14 = peaks14.getPeaks().get(i);
				Peak peak16 = peaks16.getPeaks().get(i);

				if (peak14.toString().equals(peak16.toString())) {
					continue;
				}
				
				if(!peak16.toString().contains("0,4A")){
					continue;
				}

				sb
						.append(peak14.getMz() + "\t"
								+ df2.format(peak14.getIntensity() * 100 / maxPeakIntensity)
								+ "\t");

				for (String annotation : peak14.getAnnotations()) {
					sb.append(annotation + " ");
				}
				sb.append("\t");
				for (String annotation : peak16.getAnnotations()) {
					sb.append(annotation + " ");
				}

				pw.println(sb.toString().trim());
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultPeakFile });
	}

	private PeakList<Peak> getAnnotatedPeakList(String originSeqFile,
			String originPeakFile, int assumptConnection) throws Exception {
		PeakList<Peak> result = reader.read(originPeakFile);
		result.clearAnnotation();

		Glycan glycan = GlycanUtils.readAs(glycanReader, originSeqFile, assumptConnection);

		List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();
		for (IMonosaccharide os : oss) {
			Map<FragmentationType, Peak> virtualPeaksMap = builder.buildMap(os);
			virtualPeaksMap.remove(FragmentationType.B);
			virtualPeaksMap.remove(FragmentationType.C);
			virtualPeaksMap.remove(FragmentationType.Y);
			virtualPeaksMap.remove(FragmentationType.Z);

			List<Peak> virtualPeaks = new ArrayList<Peak>(virtualPeaksMap.values());

			for (Peak virtualPeak : virtualPeaks) {
				for (Peak peak : result.getPeaks()) {
					if (Math.abs(peak.getMz() - virtualPeak.getMz()) < THRESHOLD) {
						if (virtualPeak.getIntensity() < peak.getIntensity()) {
							virtualPeak.setIntensity(peak.getIntensity());
						}
					}
				}
			}

			Collections.sort(virtualPeaks, new Comparator<Peak>() {
				public int compare(Peak arg0, Peak arg1) {
					return -Double.compare(arg0.getIntensity(), arg1.getIntensity());
				}
			});

			for (int i = 0; i < virtualPeaks.size(); i++) {
				Peak peak = virtualPeaks.get(i);
				for (int j = 0; j < peak.getAnnotations().size(); j++) {
					String oldAnnotation = peak.getAnnotations().get(j);
					String newAnnotation = oldAnnotation + "(" + (i + 1) + ")";
					peak.getAnnotations().set(j, newAnnotation);
				}
			}

			for (Peak peak : result.getPeaks()) {
				for (Peak virtualPeak : virtualPeaks) {
					if (Math.abs(peak.getMz() - virtualPeak.getMz()) < THRESHOLD) {
						peak.getAnnotations().addAll(virtualPeak.getAnnotations());
					}
				}
			}
		}
		return result;
	}
}
