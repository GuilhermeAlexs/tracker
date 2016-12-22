package model;

import java.util.List;

public class Statistics {
	private int id;
	
	private double length;
	
	private double maxElevation;
	private double minElevation;
	private double avgElevation;
	
	private double elevationGain;
	private double elevationLoss;
	
	private List<Double> inclinations;
	private double maxInclinationPositive;
	private double maxInclinationNegative;
	private double avgInclinationPositive;
	private double avgInclinationNegative;
	
	
	public Statistics() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Double> getInclinations() {
		return inclinations;
	}

	public void setInclinations(List<Double> inclinations) {
		this.inclinations = inclinations;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getMaxElevation() {
		return maxElevation;
	}

	public void setMaxElevation(double maxElevation) {
		this.maxElevation = maxElevation;
	}

	public double getMinElevation() {
		return minElevation;
	}

	public void setMinElevation(double minElevation) {
		this.minElevation = minElevation;
	}

	public double getElevationGain() {
		return elevationGain;
	}

	public void setElevationGain(double elevationGain) {
		this.elevationGain = elevationGain;
	}

	public double getElevationLoss() {
		return elevationLoss;
	}

	public void setElevationLoss(double elevationLoss) {
		this.elevationLoss = elevationLoss;
	}

	public double getAvgElevation() {
		return avgElevation;
	}

	public void setAvgElevation(double avgElevation) {
		this.avgElevation = avgElevation;
	}

	public double getMaxInclinationPositive() {
		return maxInclinationPositive;
	}

	public void setMaxInclinationPositive(double maxInclinationPositive) {
		this.maxInclinationPositive = maxInclinationPositive;
	}

	public double getMaxInclinationNegative() {
		return maxInclinationNegative;
	}

	public void setMaxInclinationNegative(double maxInclinationNegative) {
		this.maxInclinationNegative = maxInclinationNegative;
	}

	public double getAvgInclinationPositive() {
		return avgInclinationPositive;
	}

	public void setAvgInclinationPositive(double avgInclinationPositive) {
		this.avgInclinationPositive = avgInclinationPositive;
	}

	public double getAvgInclinationNegative() {
		return avgInclinationNegative;
	}

	public void setAvgInclinationNegative(double avgInclinationNegative) {
		this.avgInclinationNegative = avgInclinationNegative;
	}
}
