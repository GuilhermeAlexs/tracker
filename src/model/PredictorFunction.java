package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class PredictorFunction implements UnivariateFunction{
	private double minX;
	private double maxX;
	private UnivariateFunction func;
	
	public PredictorFunction(UnivariateFunction func, double minX, double maxX){
		this.minX = minX;
		this.maxX = maxX;
		this.func = func;
	}
	
	@Override
	public double value(double x) {
		if(x < minX)
			x = minX;
		else if(x > maxX)
			x = maxX;
		
		return func.value(x);
	}
}
