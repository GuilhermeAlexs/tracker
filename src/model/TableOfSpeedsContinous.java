package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class TableOfSpeedsContinous {
	private List<Double> speeds;
	private List<Double> inclinations;
	private UnivariateFunction function;
	
	public TableOfSpeedsContinous() {
		this.speeds = new ArrayList<Double>();
		this.inclinations = new ArrayList<Double>();
	}

	public TableOfSpeedsContinous(List<Double> speeds, List<Double> inclinations) {
		this.speeds = speeds;
		this.inclinations = inclinations;
	}

	public List<Double> getSpeeds() {
		return speeds;
	}

	public void setSpeeds(List<Double> speeds) {
		this.speeds = speeds;
	}

	public List<Double> getInclinations() {
		return inclinations;
	}

	public void setInclinations(List<Double> inclinations) {
		this.inclinations = inclinations;
	}

	public UnivariateFunction getFunction() {
		return function;
	}

	public void setFunction(UnivariateFunction function) {
		this.function = function;
	}
}
