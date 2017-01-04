package model.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class PolynomialRegression implements Regression{
	private WeightedObservedPoints reg;
	
	public PolynomialRegression(){
		this.reg = new WeightedObservedPoints();
	}
	
	@Override
	public void addObservation(double x, double y) {
		reg.add(x, y);
	}

	@Override
	public UnivariateFunction getFunction() {
		PolynomialCurveFitter fitter;
		fitter = PolynomialCurveFitter.create(2);
		double[] coeff = fitter.fit(reg.toList());
		return new PolynomialFunction(coeff);
	}	

}
