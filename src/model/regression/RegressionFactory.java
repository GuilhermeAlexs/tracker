package model.regression;

import model.BehaviorType;

public class RegressionFactory {
	public static Regression createRegression(BehaviorType type){
		if(type == BehaviorType.LINEAR)
			return new LinearRegression();
		else
			return new PolynomialRegression();
	}
}
