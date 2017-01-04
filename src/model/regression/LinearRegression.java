package model.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import model.LinearFunction;

public class LinearRegression implements Regression{
	private SimpleRegression reg;

	public LinearRegression(){
		this.reg = new SimpleRegression();
	}

	@Override
	public void addObservation(double x, double y) {
		reg.addData(x, y);
	}

	@Override
	public UnivariateFunction getFunction() {
		return new LinearFunction(reg);
	}	
}
