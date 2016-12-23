package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class ToblerFunction implements UnivariateFunction{
	private double modifier;
	
	public ToblerFunction(double modifier) {
		this.modifier = modifier;
	}

	public double getModifier() {
		return modifier;
	}

	public void setModifier(double modifier) {
		this.modifier = modifier;
	}

	@Override
	public double value(double x) {
		//x = Math.tan(Math.toRadians(x));
		return modifier*6*Math.pow(Math.E,-3.5*Math.abs(x + 0.05));
	}
}
