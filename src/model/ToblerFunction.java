package model;

import org.apache.commons.math3.analysis.UnivariateFunction;

//Simula uma fun��o Tobler usando as interfaces da Apache Math Commons.
//Dessa forma � poss�vel usar v�rias fun��es diferentes (de interpola��o,
//de regress�o e esta) dentro de uma mesma lista.
public class ToblerFunction implements UnivariateFunction{
	private double maxSpeed = 4.3f;
	
	public ToblerFunction(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@Override
	public double value(double x) {
		return maxSpeed*Math.pow(Math.E,-3.5d*Math.abs(x + 0.05d));
	}
}

