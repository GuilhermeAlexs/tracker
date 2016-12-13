package model;

public class TableOfSpeeds {
	private double [][] speeds;
	private double [][] counts;
	private String [] types;
	
	public TableOfSpeeds(double[][] speeds, double[][] counts, String types []) {
		this.speeds = speeds;
		this.counts = counts;
		this.types = types;
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

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}
}
