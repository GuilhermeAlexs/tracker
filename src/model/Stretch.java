package model;

import java.text.ParseException;

import utils.DateUtils;
import utils.GeoUtils;

public class Stretch {
	private TPLocation start;
	private TPLocation end;
	private double distance; //m
	private double diffAltitude; //m
	private double inclination; //graus
	private double speed; //m por s
	private double time; //s
	
	public Stretch(TPLocation start, TPLocation end) {
		set(start,end);
	}
	
	public TPLocation getStart() {
		return start;
	}
	
	public void set(TPLocation start, TPLocation end) {
		this.start = start;
		this.end = end;
		performCalculations();
	}
	
	public TPLocation getEnd() {
		return end;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public double getInclination() {
		return inclination;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getTime() {
		return time;
	}
	
	public double getDiffAltitude() {
		return diffAltitude;
	}

	private void performCalculations(){
		distance = GeoUtils.computeDistance(end.getLatitude(), end.getLongitude(), 
				start.getLatitude(), start.getLongitude());
		
		diffAltitude = (end.getAltitude() - start.getAltitude());
		
		inclination = diffAltitude/(double)distance;
		
		try {
			time = (DateUtils.toCalendar(end.getWhen()).getTimeInMillis() - 
					DateUtils.toCalendar(start.getWhen()).getTimeInMillis())/(double)1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		speed = Math.abs(distance/(double)time);
	}
}
