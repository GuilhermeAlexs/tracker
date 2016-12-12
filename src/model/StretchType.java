package model;

import java.awt.Color;

public class StretchType {
	private String id;
	private String name;
	private Color color;
	
	public StretchType() {
	}

	public StretchType(String id, String name, Color color) {
		this.id = id;
		this.name = name;
		this.color = color;
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
}
