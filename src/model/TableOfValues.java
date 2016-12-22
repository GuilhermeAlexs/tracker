package model;

public class TableOfValues {
	private double [][] values;
	private double [][] counts;
	
	public TableOfValues(double[][] values, double[][] counts) {
		this.values = values;
		this.counts = counts;
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
}
