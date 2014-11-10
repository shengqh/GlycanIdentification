package edu.iu.informatics.omics.tools;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.rcpa.bio.processor.IFileProcessor;
import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.IGlycanModifier;
import edu.iu.informatics.omics.impl.AverageMassDefinitionImpl;
import edu.iu.informatics.omics.impl.PeracetylatedGlycanModifierImpl;
import edu.iu.informatics.omics.impl.PermethylatedGlycanModifierImpl;
import edu.iu.informatics.omics.impl.UnderivatisedGlycanModifierImpl;
import edu.iu.informatics.omics.io.GlycanStructureFileIterator;
import edu.iu.informatics.omics.io.IGlycanParser;

public class GlycanStructureToAtomCompositionBuilder implements IFileProcessor {
	private IGlycanParser parser;

	public GlycanStructureToAtomCompositionBuilder(IGlycanParser parser) {
		this.parser = parser;
	}

	public List<String> process(String originFile) throws Exception {
		GlycanStructureFileIterator iter = new GlycanStructureFileIterator(parser,
				originFile);

		IGlycanModifier underivatisedGlycanModifier = new UnderivatisedGlycanModifierImpl();
		IGlycanModifier permethylatedModifier = new PermethylatedGlycanModifierImpl();
		IGlycanModifier peracetylatedGlycanModifier = new PeracetylatedGlycanModifierImpl();

		IGlycanModifier[] modifiers = new IGlycanModifier[] {
				underivatisedGlycanModifier, permethylatedModifier,
				peracetylatedGlycanModifier };

		Map<IGlycanModifier, Map<String, Double>> massMap = new HashMap<IGlycanModifier, Map<String, Double>>();
		for (IGlycanModifier modifier : modifiers) {
			massMap.put(modifier, new HashMap<String, Double>());
		}

		AverageMassDefinitionImpl mdImpl = new AverageMassDefinitionImpl();

		DecimalFormat df = new DecimalFormat("0.0000");

		String resultFile = originFile + ".atomcomposition";
		PrintWriter pw = new PrintWriter(resultFile);
		try {
			pw
					.println("Name\tFormula\tMonosaccharideComposition\tUnderivatisedAtomComposition\tUnderivatisedAverageMass\tPermethylatedAtomComposition\tPermethylatedAverageMass\tPeracetylatedAtomComposition\tPeracetylatedAverageMass");
			while (iter.hasNext()) {
				Glycan glycan = iter.next();
				String glycanStr = parser.glycanToString(glycan);
				pw.print(glycan.getName() + "\t" + glycanStr + "\t"
						+ glycan.getMonosaccharideCompositionStr());

				for (IGlycanModifier modifier : modifiers) {
					Glycan curGlycan = parser.parse(glycanStr);
					modifier.modifyGlycan(curGlycan);

					String atomCompositionStr = curGlycan.getAtomCompositionStr();

					double mass = 0.0;
					if (massMap.get(modifier).containsKey(atomCompositionStr)) {
						mass = massMap.get(modifier).get(atomCompositionStr);
					} else {
						Map<Character, Integer> atomCounts = curGlycan.getAtomCounts();
						for (Character a : atomCounts.keySet()) {
							mass += mdImpl.getAtom(a) * atomCounts.get(a);
						}
						massMap.get(modifier).put(atomCompositionStr, mass);
					}
					pw.print("\t" + atomCompositionStr + "\t" + df.format(mass));
				}
				pw.println();
			}
		} finally {
			pw.close();
		}

		return Arrays.asList(new String[] { resultFile });
	}

}
