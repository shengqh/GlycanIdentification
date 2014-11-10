package edu.iu.informatics.omics.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.MassProxyFactory;
import edu.iu.informatics.omics.impl.LinearFragmentationBuilderImpl;
import edu.iu.informatics.omics.io.IGlycanReader;
import edu.iu.informatics.omics.io.PeakListReader;
import edu.iu.informatics.omics.io.impl.MaldiPeakListReaderImpl;

public class LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator {
	private List<FragmentationType> keptIonTypes16;

	private List<FragmentationType> keptIonTypes14;

	private FragmentationBuilder<Peak> builder;

	private PeakListReader<Peak> reader = new MaldiPeakListReaderImpl();

	private IGlycanReader glycanReader;

	private int fromSite;

	private DerivativeType derivativeType;

	private static List<FragmentationType> removedTypes = Arrays
			.asList(new FragmentationType[] { FragmentationType.B,
					FragmentationType.C, FragmentationType.A13, FragmentationType.Y,
					FragmentationType.Z, FragmentationType.X13 });

	public LinearGlycanFragmentationOrderDistanceOfIonTypeCalculator(
			IGlycanReader glycanReader, IsotopicType iType, DerivativeType dType,
			AdductType aType, FragmentationType[] fTypes16,
			FragmentationType[] fTypes14, int fromSite) {
		this.glycanReader = glycanReader;
		this.keptIonTypes16 = new ArrayList<FragmentationType>(Arrays
				.asList(fTypes16));
		this.keptIonTypes14 = new ArrayList<FragmentationType>(Arrays
				.asList(fTypes14));
		this.derivativeType = dType;

		IMassProxy massProxy = MassProxyFactory.getMassProxy(iType, dType, aType);
		this.builder = new LinearFragmentationBuilderImpl(massProxy);
		this.fromSite = fromSite;
	}

	private List<FragmentationType> getKeptIonTypes(int assumptConnectionType) {
		if (6 == assumptConnectionType) {
			return keptIonTypes16;
		} else if (4 == assumptConnectionType) {
			return keptIonTypes14;
		} else {
			throw new IllegalArgumentException(
					"Don't know how to get KeptIonTypes for 1," + assumptConnectionType);
		}
	}

	public String getIonTypesString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < keptIonTypes16.size(); i++) {
			if (0 == i) {
				sb.append(keptIonTypes16.get(i).toString());
			} else {
				sb.append("_" + keptIonTypes16.get(i).toString());
			}
		}
		sb.append(")");

		sb.append("(");
		for (int i = 0; i < keptIonTypes14.size(); i++) {
			if (0 == i) {
				sb.append(keptIonTypes14.get(i).toString());
			} else {
				sb.append("_" + keptIonTypes14.get(i).toString());
			}
		}
		sb.append(")");

		return sb.toString();
	}

	public List<FragmentationTypeOrderEntry> getFragmentationTypeOrderEntryListFromFiles(
			String[] files, int assumptConnectionType, double peakTolerance)
			throws Exception {
		List<FragmentationTypeOrderEntry> result = new ArrayList<FragmentationTypeOrderEntry>();

		for (String file : files) {
			final String inputPeakFile = RcpaFileUtils.changeExtension(file,
					"txt.ann");
			final PeakList<Peak> inputPeaks = reader.read(inputPeakFile);

			final Glycan inputGlycan = glycanReader.read(file);

			List<FragmentationTypeOrderEntry> entries = getFragmentationTypeOrderEntryList(
					inputGlycan, inputPeaks, assumptConnectionType, peakTolerance);

			String filename = new File(file).getName();
			for (FragmentationTypeOrderEntry entry : entries) {
				entry.setFilename(filename);
			}

			result.addAll(entries);
		}
		return result;
	}

	public List<FragmentationTypeOrderEntry> getFragmentationTypeOrderEntryList(
			Glycan inputGlycan, PeakList<Peak> inputPeaks, int assumptConnectionType,
			double peakTolerance) throws Exception {
		List<FragmentationTypeOrderEntry> result = new ArrayList<FragmentationTypeOrderEntry>();

		Glycan assumptGlycan = GlycanUtils.parseAs(glycanReader, glycanReader
				.glycanToString(inputGlycan), assumptConnectionType);

		List<IMonosaccharide> assumptOss = assumptGlycan
				.getOligosaccharidesFromNonreducingTerm();

		List<IMonosaccharide> inputOss = inputGlycan
				.getOligosaccharidesFromNonreducingTerm();

		for (int i = fromSite; i < assumptOss.size() - 1; i++) {
			Map<FragmentationType, Peak> virtualPeaks = builder.buildMap(assumptOss
					.get(i));

			Map<FragmentationType, Integer> orders = GlycanUtils.getOrderMap(
					removedTypes, getKeptIonTypes(assumptConnectionType), virtualPeaks,
					inputPeaks, peakTolerance);

			Map<Integer, IMonosaccharide> osMap = inputOss.get(i)
					.getSubMonosaccharides();

			FragmentationTypeOrderEntry entry = new FragmentationTypeOrderEntry();
			int inputConnectionType = osMap.keySet().iterator().next();
			entry.setInputLinkageType(inputConnectionType);
			entry.setPositionFromNonreducingTerm(i + 1);
			entry.setTypeOrder(orders);
			entry.setMonosaccharideName(assumptOss.get(i).getShortName());
			result.add(entry);
		}
		return result;
	}

	public IGlycanReader getGlycanReader() {
		return glycanReader;
	}

	public int getFromSite() {
		return fromSite;
	}

	public DerivativeType getDerivativeType() {
		return derivativeType;
	}

	public void setFromSite(int fromSite) {
		this.fromSite = fromSite;
	}
}
