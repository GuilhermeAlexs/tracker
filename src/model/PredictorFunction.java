package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class PredictorFunction implements UnivariateFunction{
	private double minX;
	private double maxX;
	private UnivariateFunction func;
	private StretchType type;
	
	public PredictorFunction(UnivariateFunction func, double minX, double maxX){
		this.minX = minX;
		this.maxX = maxX;
		this.func = func;
	}
	
	public PredictorFunction(UnivariateFunction func, double minX, double maxX, StretchType type){
		this.minX = minX;
		this.maxX = maxX;
		this.func = func;
		this.type = type;
	}
		
	public StretchType getType() {
		return type;
	}

	public void setType(StretchType type) {
		this.type = type;
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
