package model;

public class Performance {
	private double inclination;
	private double speed;
	private int counter;
	
	public Performance() {
		super();
		
		this.counter = 0;
	}

	public double getInclination() {
		return inclination;
	}

	public void setInclination(double inclination) {
		this.inclination = inclination;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void incrementCounter(){
		this.counter++;
	}
}
