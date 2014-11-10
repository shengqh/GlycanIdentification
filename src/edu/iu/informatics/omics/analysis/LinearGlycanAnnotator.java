package edu.iu.informatics.omics.analysis;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.image.SpectrumImageBuilder;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.PeakListWriter;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListAnnotationWriterImpl;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class LinearGlycanAnnotator {
	private static final double THRESHOLD = 0.3;

	private IsotopicType isotopicType;

	private IMassProxy massProxy;

	private FragmentationBuilder<Peak> builder;

	private SpectrumImageBuilder imageBuilder;

	private PeakListReader<Peak> reader;

	private PeakListWriter<Peak> writer;

	private IGlycanReader glycanReader;

	public LinearGlycanAnnotator(IGlycanReader glycanReader, IsotopicType iType,
			DerivativeType dType, AdductType pType, PeakListReader<Peak> reader,
			PeakListWriter<Peak> writer) {
		super();
		this.glycanReader = glycanReader;
		this.isotopicType = iType;
		this.reader = reader;
		this.writer = writer;
		massProxy = MassProxyFactory.getMassProxy(iType, dType, pType);
		builder = new LinearFragmentationBuilderImpl(massProxy);
		imageBuilder = new SpectrumImageBuilder(new Dimension(4800, 600),
				Color.BLACK, Color.RED);
	}

	public static void main(String[] args) throws Exception {
		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();
		PeakListWriter<Peak> writer = new MaldiPeakListAnnotationWriterImpl<Peak>();

		annotateUnderivatised(reader, writer);
		annotatePermethylated(reader, writer);
	}

	private static void annotatePermethylated(PeakListReader<Peak> reader, PeakListWriter<Peak> writer) throws Exception {
		File[] files = new File("F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\NewPermethylated")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith("inp");
					}
				});

		new LinearGlycanAnnotator(new GlycanColFormat(), IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, AdductType.Na, reader, writer)
				.match(files);
	}

	private static void annotateUnderivatised(PeakListReader<Peak> reader, PeakListWriter<Peak> writer) throws Exception {
		File[] files = new File("F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\Underivatised")
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith("inp");
					}
				});

		new LinearGlycanAnnotator(new GlycanColFormat(), IsotopicType.Average,
				DerivativeType.Underivatised, AdductType.Na, reader, writer)
				.match(files);
	}

	public void match(File[] files) throws Exception {
		for (File file : files) {
			String originPeakFile = RcpaFileUtils.changeExtension(file
					.getAbsolutePath(), "txt.ann");
			PeakList<Peak> originPeaks = reader.read(originPeakFile);
			originPeaks.clearAnnotation();

			Glycan glycan = glycanReader.read(file.getAbsolutePath());

			PeakList<Peak> virtualPeaks = builder.build(glycan);
			for (Peak peak : originPeaks.getPeaks()) {
				for (Peak virtualPeak : virtualPeaks.getPeaks()) {
					if (Math.abs(peak.getMz() - virtualPeak.getMz()) < THRESHOLD) {
						peak.getAnnotations().addAll(virtualPeak.getAnnotations());
					}
				}
			}

			for (int i = 0; i < originPeaks.getPeaks().size(); i++) {
				Peak pi = originPeaks.getPeaks().get(i);
				for (int j = i + 1; j < originPeaks.getPeaks().size(); j++) {
					Peak pj = originPeaks.getPeaks().get(j);
					if (pi.getAnnotations().equals(pj.getAnnotations())) {
						if (pi.getIntensity() > pj.getIntensity()) {
							pj.setAnnotations(null);
							continue;
						}
						pi.setAnnotations(null);
						break;
					}
				}
			}

			String resultPeakFile = RcpaFileUtils.changeExtension(file
					.getAbsolutePath(), "txt." + isotopicType + ".ann");
			writer.write(resultPeakFile, originPeaks);

			imageBuilder.setPeaks(originPeaks);
			String imagePeakFile = RcpaFileUtils.changeExtension(file
					.getAbsolutePath(), "txt." + isotopicType + ".png");
			imageBuilder.drawImage(imagePeakFile);
		}
	}
}
