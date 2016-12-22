package model;

import java.io.Serializable;

public enum LocationType  implements Serializable{
	ROAD(0,"Estrada"), TRAIL(1, "Trilha"), RIVER(2, "Rio"), SNOW(3, "Neve"), FOREST(4, "Floresta"), INVALID(5, "Invalido"), DESERT(5, "Deserto");
	
	private int value;
	private String name;
	
	private LocationType(int val, String name){
		this.value = val;
		this.name = name;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getName(){
		return this.name;
	}
	
	public static LocationType getTypeFromValue(int i){
		if(i == 0)
			return ROAD;
		else if(i == 1)
			return TRAIL;
		else if(i == 2)
			return RIVER;
		else if(i == 3)
			return SNOW;
		else if(i == 4)
			return FOREST;
		
		return INVALID;
	}
}
