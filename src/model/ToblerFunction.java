package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

//Simula uma função Tobler usando as interfaces da Apache Math Commons.
//Dessa forma é possível usar várias funções diferentes (de interpolação,
//de regressão e esta) dentro de uma mesma lista.
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
		return 4.3*Math.pow(Math.E,-3.5d*Math.abs(x + 0.05d));
	}
}

