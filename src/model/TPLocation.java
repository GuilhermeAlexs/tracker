package model;

import java.io.Serializable;

public class TPLocation implements Serializable{	
	private static final long serialVersionUID = -1674471144939898278L;
	
	private int id;
	private double latitude;
	private double longitude;
	private double altitude = -1;
	private String typeId;
	private String when;
	private boolean selected;
	
	public TPLocation() {
		this.selected = false;
		this.typeId = TypeConstants.FIXED_TYPE_TRAIL;
	}
	
	public TPLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.selected = false;
		this.typeId = TypeConstants.FIXED_TYPE_TRAIL;
	}
	
	public TPLocation(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.selected = false;
		this.typeId = TypeConstants.FIXED_TYPE_TRAIL;
	}
	
	public TPLocation(double latitude, double longitude, double altitude, String when) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.when = when;
		this.selected = false;
		this.typeId = TypeConstants.FIXED_TYPE_TRAIL;
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

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
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
