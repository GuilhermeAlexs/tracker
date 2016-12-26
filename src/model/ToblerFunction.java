package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

//Simula uma fun��o Tobler usando as interfaces da Apache Math Commons.
//Dessa forma � poss�vel usar v�rias fun��es diferentes (de interpola��o,
//de regress�o e esta) dentro de uma mesma lista.
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
		//x = Math.tan(Math.toRadians(x))
		return 4.5*Math.pow(Math.E,-3.5d*Math.abs(x + 0.05d));
	}
}

