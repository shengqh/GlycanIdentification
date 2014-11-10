package edu.iu.informatics.omics.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.proteomics.Peak;
import cn.ac.rcpa.bio.proteomics.PeakList;
import edu.iu.informatics.omics.FragmentationBuilder;
import edu.iu.informatics.omics.FragmentationType;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IMassProxy;
import edu.iu.informatics.omics.IMonosaccharide;
import edu.iu.informatics.omics.IMonosaccharideFragmentationBuilder;
import edu.iu.informatics.omics.impl.fragmentation.OligosaccharideFragmentationBuilderFactory;

public class LinearFragmentationBuilderImpl implements FragmentationBuilder<Peak> {
	private static double PEAK_THRESHOLD = 0.00001;

	private IMassProxy massProxy;

	Map<String, List<IMonosaccharideFragmentationBuilder>> builderMap;

	public LinearFragmentationBuilderImpl(IMassProxy massProxy) {
		super();
		this.massProxy = massProxy;

		builderMap = new HashMap<String, List<IMonosaccharideFragmentationBuilder>>();

		List<IMonosaccharideFragmentationBuilder> hexoseBuilders = new ArrayList<IMonosaccharideFragmentationBuilder>();
		for (FragmentationType type : FragmentationType.items) {
			hexoseBuilders.add(OligosaccharideFragmentationBuilderFactory.createHexoseFragmentationBuilder(type));
		}

		List<IMonosaccharideFragmentationBuilder> neuBuilders = new ArrayList<IMonosaccharideFragmentationBuilder>();
		for (FragmentationType type : FragmentationType.items) {
			neuBuilders.add(OligosaccharideFragmentationBuilderFactory.createNeuFragmentationBuilder(type));
		}

		builderMap.put(Hexose.class.getName(), hexoseBuilders);
		builderMap.put(NeuAc.class.getName(), neuBuilders);
	}

	public double calculatePrecursor(Glycan glycan) {
		return massProxy.getAdduct() + glycan.getReducingTerm().getMassAll(massProxy.getMassDefinition());
	}

	private static DecimalFormat df = new DecimalFormat("0.0000");

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(massProxy.toString());
		sb.append("\nAdductMass=" + df.format(massProxy.getAdduct()));
		return sb.toString();
	}

	public PeakList<Peak> build(Glycan glycan) {
		if (!glycan.isLinear()) {
			throw new IllegalArgumentException("Glycan " + glycan + " is not linear");
		}

		massProxy.modifyGlycan(glycan);

		PeakList<Peak> result = new PeakList<Peak>();
		List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();

		result.setPrecursorAndCharge(calculatePrecursor(glycan), 1);

		for (IMonosaccharide os : oss) {
			List<IMonosaccharideFragmentationBuilder> builders = findBuilders(os);

			for (IMonosaccharideFragmentationBuilder builder : builders) {
				result.addOrMergePeak(builder.build(massProxy, os), PEAK_THRESHOLD);
			}
		}

		result.sort();
		if (result.getPeaks().size() > 0 && (0.0 == result.getPeaks().get(0).getMz())) {
			result.getPeaks().remove(0);
		}
		return result;
	}

	private List<IMonosaccharideFragmentationBuilder> findBuilders(IMonosaccharide os) {
		List<IMonosaccharideFragmentationBuilder> builders = builderMap.get(os.getClass().getName());
		if (builders == null) {
			throw new IllegalStateException("Cannot find builder for " + os.getClass());
		}
		return builders;
	}

	public Map<FragmentationType, List<Peak>> buildMap(Glycan glycan) {
		if (!glycan.isLinear()) {
			throw new IllegalArgumentException("Glycan " + glycan + " is not linear");
		}

		massProxy.modifyGlycan(glycan);

		Map<FragmentationType, List<Peak>> result = new HashMap<FragmentationType, List<Peak>>();
		List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();
		for (IMonosaccharide os : oss) {
			Map<FragmentationType, Peak> peakMap = buildMap(os);
			for (FragmentationType type : peakMap.keySet()) {
				if (!result.containsKey(type)) {
					result.put(type, new ArrayList<Peak>());
				}

				result.get(type).add(peakMap.get(type));
			}
		}
		return result;
	}

	public IMassProxy getMassProxy() {
		return massProxy;
	}

	public Map<FragmentationType, Peak> buildMap(IMonosaccharide os) {
		massProxy.modifyOligosaccharide(os, true);

		Map<FragmentationType, Peak> result = new HashMap<FragmentationType, Peak>();

		List<IMonosaccharideFragmentationBuilder> builders = findBuilders(os);
		for (IMonosaccharideFragmentationBuilder builder : builders) {
			Peak peak = builder.build(massProxy, os);
			result.put(builder.getType(), peak);
		}
		return result;
	}

	public Map<String, Peak> buildPeakMap(Glycan glycan) {
		final Map<FragmentationType, List<Peak>> fragmentationMap = buildMap(glycan);

		final Map<String, Peak> result = new HashMap<String, Peak>();
		for (List<Peak> peaks : fragmentationMap.values()) {
			for (Peak peak : peaks) {
				result.put(peak.getAnnotations().get(0), peak);
			}
		}
		return result;
	}

	public Map<FragmentationType, List<Peak>> buildMapWithoutTerminalOligosaccharide(Glycan glycan) {
		if (!glycan.isLinear()) {
			throw new IllegalArgumentException("Glycan " + glycan + " is not linear");
		}

		massProxy.modifyGlycan(glycan);

		final Map<FragmentationType, List<Peak>> result = new HashMap<FragmentationType, List<Peak>>();
		final List<IMonosaccharide> oss = glycan.getOligosaccharidesFromNonreducingTerm();
		for (int i = 1; i < oss.size() - 1; i++) {
			final IMonosaccharide os = oss.get(i);
			final Map<FragmentationType, Peak> peakMap = buildMap(os);
			for (FragmentationType type : peakMap.keySet()) {
				if (!result.containsKey(type)) {
					result.put(type, new ArrayList<Peak>());
				}

				result.get(type).add(peakMap.get(type));
			}
		}
		return result;
	}
}