package edu.iu.informatics.omics.analysis;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.IsotopicType;
import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import cn.ac.rcpa.bio.proteomics.image.SpectrumImageBuilder;
import cn.ac.rcpa.utils.RcpaFileUtils;
import edu.iu.informatics.omics.AdductType;
import edu.iu.informatics.omics.DerivativeType;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class OligosaccharideAnnotator {
	private static final double THRESHOLD = 0.3;

	private IsotopicType isotopicType;

	private IMassProxy massProxy;

	private FragmentationBuilder<Peak> builder;

	private PeakListReader<Peak> reader;

	private IGlycanReader glycanReader;

	public OligosaccharideAnnotator(IGlycanReader glycanReader,
			IsotopicType iType, DerivativeType dType, AdductType pType,
			PeakListReader<Peak> reader) {
		super();
		this.glycanReader = glycanReader;
		this.isotopicType = iType;
		this.reader = reader;
		massProxy = MassProxyFactory.getMassProxy(iType, dType, pType);
		builder = new LinearFragmentationBuilderImpl(massProxy);
	}

	public static void main(String[] args) throws Exception {
		PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();
		IGlycanReader glycanReader = new GlycanColFormat();
		new OligosaccharideAnnotator(glycanReader, IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, AdductType.Na, reader).match(new File(
				"F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\NewPermethylated\\Permethylated_Dextran_glc09.inp"), 5);
		new OligosaccharideAnnotator(glycanReader, IsotopicType.Monoisotopic,
				DerivativeType.Permethylated, AdductType.Na, reader).match(new File(
				"F:\\sqh\\Project\\glycan\\yehia\\MaldiTof\\NewPermethylated\\Permethylated_Maltooligosaccharides_glc09.inp"), 5);
	}

	public void match(File file, int positionFromNonreducing) throws Exception {
		String originPeakFile = RcpaFileUtils.changeExtension(file
				.getAbsolutePath(), "txt.ann");
		PeakList<Peak> originPeaks = reader.read(originPeakFile);
		originPeaks.clearAnnotation();

		Glycan glycan = glycanReader.read(file.getAbsolutePath());

		List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();

		Map<FragmentationType, Peak> virtualPeaks = builder.buildMap(oss
				.get(positionFromNonreducing));
		
		virtualPeaks.remove(FragmentationType.Z);
		virtualPeaks.remove(FragmentationType.C);
		virtualPeaks.remove(FragmentationType.B);
		virtualPeaks.remove(FragmentationType.Y);
		
		for (Peak peak : originPeaks.getPeaks()) {
			for (FragmentationType atype : virtualPeaks.keySet()) {
				Peak virtualPeak = virtualPeaks.get(atype);
				if (Math.abs(peak.getMz() - virtualPeak.getMz()) < THRESHOLD) {
					peak.getAnnotations().add(atype.toString());
				}
			}
		}
		
		for (Peak peak1 : originPeaks.getPeaks()) {
			if (peak1.getAnnotations().isEmpty()) {
				continue;
			}
			for (Peak peak2 : originPeaks.getPeaks()) {
				if (peak2.getAnnotations().isEmpty()) {
					continue;
				}
				if (peak1 != peak2
						&& peak1.getAnnotations().equals(peak2.getAnnotations())) {
					if (peak1.getIntensity() > peak2.getIntensity()) {
						peak2.getAnnotations().clear();
					} else {
						peak1.getAnnotations().clear();
						break;
					}
				}
			}
		}

		String resultPeakFile = RcpaFileUtils.changeExtension(file
				.getAbsolutePath(), "txt." + isotopicType + "."
				+ positionFromNonreducing + ".annotation");
		PrintWriter pw = new PrintWriter(resultPeakFile);
		try {
			pw.println(originPeaks);
		} finally {
			pw.close();
		}

		drawPeaks(originPeaks, virtualPeaks, resultPeakFile, true);
		drawPeaks(originPeaks, virtualPeaks, resultPeakFile, false);
	}

	private void drawPeaks(PeakList<Peak> originPeaks,
			Map<FragmentationType, Peak> virtualPeaks, String resultPeakFile,
			boolean isNonReducingSeries) throws Exception {
		originPeaks.clearAnnotation();
		for (Peak peak : originPeaks.getPeaks()) {
			for (FragmentationType atype : virtualPeaks.keySet()) {
				if (atype.isNonReducingSeries() == isNonReducingSeries) {
					Peak virtualPeak = virtualPeaks.get(atype);
					if (Math.abs(peak.getMz() - virtualPeak.getMz()) < THRESHOLD) {
						peak.getAnnotations().add(atype.toString());
					}
				}
			}
		}

		for (Peak peak1 : originPeaks.getPeaks()) {
			if (peak1.getAnnotations().isEmpty()) {
				continue;
			}
			for (Peak peak2 : originPeaks.getPeaks()) {
				if (peak2.getAnnotations().isEmpty()) {
					continue;
				}
				if (peak1 != peak2
						&& peak1.getAnnotations().equals(peak2.getAnnotations())) {
					if (peak1.getIntensity() > peak2.getIntensity()) {
						peak2.getAnnotations().clear();
					} else {
						peak1.getAnnotations().clear();
						break;
					}
				}
			}
		}

		PeakList<Peak> drawPeaks = new PeakList<Peak>();
		PeakList<Peak> tmpPeaks = new PeakList<Peak>();

		boolean first = true;
		for (Peak peak : originPeaks.getPeaks()) {
			if (peak.getAnnotations().isEmpty()) {
				if (!first) {
					tmpPeaks.getPeaks().add(peak);
				}
				continue;
			}

			drawPeaks.getPeaks().addAll(tmpPeaks.getPeaks());
			tmpPeaks.getPeaks().clear();
			first = false;
			drawPeaks.getPeaks().add(peak);
		}
		
		SpectrumImageBuilder imageBuilder = new SpectrumImageBuilder(new Dimension(400,300), Color.BLACK, Color.BLACK);
		imageBuilder.setPeaks(drawPeaks);
		imageBuilder.setFixMaxIntensity(originPeaks.getMaxIntensity());

		System.out.println(drawPeaks);
		FileOutputStream fos = new FileOutputStream(resultPeakFile
				+ (isNonReducingSeries ? ".BSeries.png" : ".YSeries.png"));
		imageBuilder.drawImage(fos);
		fos.close();
	}
}
