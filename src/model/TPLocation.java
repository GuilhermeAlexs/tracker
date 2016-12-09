package model;

import java.io.Serializable;

public class TPLocation implements Serializable{
	private int id;
	private double latitude;
	private double longitude;
	private double altitude;
	private LocationType type;
	private String when;
	private boolean selected;
	
	public TPLocation() {
		super();
		this.selected = false;
		this.type = LocationType.TRAIL;
	}
	
	public TPLocation(double latitude, double longitude, double altitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.selected = false;
		this.type = LocationType.TRAIL;
	}
	
	public TPLocation(double latitude, double longitude, double altitude, String when) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.when = when;
		this.selected = false;
		this.type = LocationType.TRAIL;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
