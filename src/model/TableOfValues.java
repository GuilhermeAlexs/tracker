package model;

public class TableOfValues {
	private double [][] values;
	private double [][] counts;
	private double restProportion;
	
	public TableOfValues(double[][] values, double[][] counts, double restProportion) {
		this.values = values;
		this.counts = counts;
		this.restProportion = restProportion;
	}

	public double[][] getValues() {
		return values;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}

	public double[][] getCounts() {
		return counts;
	}

	public void setCounts(double[][] counts) {
		this.counts = counts;
	}

	public double getRestProportion() {
		return restProportion;
	}

	public void setRestProportion(double restProportion) {
		this.restProportion = restProportion;
	}
}
