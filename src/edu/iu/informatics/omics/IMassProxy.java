package edu.iu.informatics.omics;

public interface IMassProxy extends IGlycanModifier {
	IMassDefinition getMassDefinition();

	double getCHON(int C, int H, int O, int N);

	double getCHO(int C, int H, int O);

	double getH2O();

	double getAdduct();

	void setAdduct(AdductType value);
}
