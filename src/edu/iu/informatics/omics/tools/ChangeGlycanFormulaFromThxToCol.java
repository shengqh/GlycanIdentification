package edu.iu.informatics.omics.tools;

import java.io.File;
import java.io.FilenameFilter;

import edu.iu.informatics.omics.Glycan;
import edu.iu.informatics.omics.io.impl.GlycanColFormat;
import edu.iu.informatics.omics.io.impl.GlycanThxFormat;

public class ChangeGlycanFormulaFromThxToCol {
	public static void main(String[] args) {
		File dir1 = new File("F:\\sqh\\Project\\glycan\\yehia\\Underivatised");
		File dir2 = new File("F:\\sqh\\Project\\glycan\\yehia\\Permethylated");
		File dir3 = new File("F:\\sqh\\Project\\glycan\\yehia\\NewPermethylated");
		replace(dir1);
		replace(dir2);
		replace(dir3);
	}

	private static void replace(File dir) {
		File[] inpFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".inp");
			}
		});
		
		GlycanThxFormat gtf = new GlycanThxFormat();
		GlycanColFormat gcf = new GlycanColFormat();
		
		for(File inpFile:inpFiles){
			System.out.println(inpFile.getName());
			try{
				Glycan glycan = gtf.read(inpFile.getAbsolutePath());
				gcf.write(inpFile.getAbsolutePath(), glycan);
			}
			catch(Exception ex){
			}
		}
	}
}
