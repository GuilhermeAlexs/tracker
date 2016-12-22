package model;

import java.awt.Color;
import java.io.Serializable;

public class StretchType implements Serializable {
	private static final long serialVersionUID = 4389283309603615536L;
	
	private String id;
	private String name;
	private Color color;
	private BehaviorType behaviorType;
	
	public StretchType() {
	}

	public StretchType(String id, String name, Color color, BehaviorType behaviorType) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.behaviorType = behaviorType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public BehaviorType getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(BehaviorType behaviorType) {
		this.behaviorType = behaviorType;
	}
}
