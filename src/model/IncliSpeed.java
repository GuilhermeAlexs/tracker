package model;

public class IncliSpeed {
	private double speed;
	private double inclination;
	
	public IncliSpeed(double inclination, double speed) {
		this.speed = speed;
		this.inclination = inclination;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getInclination() {
		return inclination;
	}

	public void setInclination(double inclination) {
		this.inclination = inclination;
	}
}
