package model;

public class TableOfSpeeds {
	private double [][] speeds;
	private double [][] counts;
	
	public TableOfSpeeds(double[][] speeds, double[][] counts) {
		super();
		this.speeds = speeds;
		this.counts = counts;
	}

	public double[][] getSpeeds() {
		return speeds;
	}

	public void setSpeeds(double[][] speeds) {
		this.speeds = speeds;
	}

	public double[][] getCounts() {
		return counts;
	}

	public void setCounts(double[][] counts) {
		this.counts = counts;
	}
}
