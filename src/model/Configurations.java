package model;

import java.awt.Color;
import java.io.Serializable;

import database.DatabaseManager;

public class Configurations implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Configurations INSTANCE;
	
	private double minimumSpeed;
	private double maximumSpeed;
	private double restTime;
	private double steps;
	private Color elevationGraphColor;
	private Color speedGraphColor;
	private Color selectionColor;
	private String proxyAddress = "";
	private String proxyUser = "";
	private String proxyPassword = "";
	private String proxyPort = "";
	
	private Configurations(double minimumSpeed, double maximumSpeed, double restTime, double steps, Color elevationGraphColor,
			Color speedGraphColor, Color selectionColor) {
		this.minimumSpeed = minimumSpeed;
		this.maximumSpeed = maximumSpeed;
		this.restTime = restTime;
		this.steps = steps;
		this.elevationGraphColor = elevationGraphColor;
		this.speedGraphColor = speedGraphColor;
		this.selectionColor = selectionColor;
	}

	public static Configurations getInstance(){
		if(INSTANCE == null){
			INSTANCE = DatabaseManager.getInstance().loadConfigurations();

			if(INSTANCE == null)
				INSTANCE = new Configurations(0.2*3.6d, 10*3.6, 15*60d, 1, new Color(221, 141, 22), new Color(163, 194, 224, 90), Color.RED);
		}

		return INSTANCE;
	}
	
	public static Configurations getInstance(double minimumSpeed, double maximumSpeed, double restTime, double steps, Color elevationGraphColor,
			Color speedGraphColor, Color selectionColor){
		if(INSTANCE == null){
			INSTANCE = DatabaseManager.getInstance().loadConfigurations();

			if(INSTANCE == null)
				INSTANCE = new Configurations(0.2*3.6d, 10*3.6, 15*60d, 1, new Color(221, 141, 22), new Color(163, 194, 224, 90), Color.RED);
		}

		return INSTANCE;
	}

	public double getMinimumSpeed() {
		return minimumSpeed;
	}

	public void setMinimumSpeed(double minimumSpeed) {
		this.minimumSpeed = minimumSpeed;
	}

	public double getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(double maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}
	
	public double getRestTime() {
		return restTime;
	}

	public void setRestTime(double restTime) {
		this.restTime = restTime;
	}

	public double getSteps() {
		return steps;
	}

	public void setSteps(double steps) {
		this.steps = steps;
	}

	public Color getElevationGraphColor() {
		return elevationGraphColor;
	}

	public void setElevationGraphColor(Color elevationGraphColor) {
		this.elevationGraphColor = elevationGraphColor;
	}

	public Color getSpeedGraphColor() {
		return speedGraphColor;
	}

	public void setSpeedGraphColor(Color speedGraphColor) {
		this.speedGraphColor = speedGraphColor;
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	public String getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
}
