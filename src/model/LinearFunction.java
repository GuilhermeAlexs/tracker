package model;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class LinearFunction implements UnivariateFunction{
	private SimpleRegression regression;
	
	public LinearFunction(SimpleRegression regression){
		this.regression = regression;
	}
	
	@Override
	public double value(double x) {
		return regression.predict(x);
	}
}
