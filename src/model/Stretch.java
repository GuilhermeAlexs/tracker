package model;

import java.text.ParseException;

import utils.DateUtils;
import utils.GeoUtils;

public class Stretch {
	private TPLocation start;
	private TPLocation end;
	private double distance; //m
	private double diffAltitude; //m
	private double inclination;
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

	public double getTheta(){
		return Math.atan2(diffAltitude, distance);
	}

	private void performCalculations(){
		distance = GeoUtils.computeDistance(start.getLatitude(), start.getLongitude(), 
				end.getLatitude(), end.getLongitude());

		diffAltitude = end.getAltitude() - start.getAltitude();

		inclination = diffAltitude/(double)distance;

		try {
			if(start.getWhen() != null && end.getWhen() != null){
				time = (DateUtils.toCalendar(end.getWhen()).getTimeInMillis() - 
					DateUtils.toCalendar(start.getWhen()).getTimeInMillis())/1000d;
				
				speed = Math.abs(distance/(double)time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
