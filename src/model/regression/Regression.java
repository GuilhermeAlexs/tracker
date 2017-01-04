package model.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface Regression{
	void addObservation(double x, double y);
	UnivariateFunction getFunction();
}
