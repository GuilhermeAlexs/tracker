package view;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DatabaseManager;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import model.BehaviorType;
import model.StretchType;
import model.TPLocation;
import model.TypeConstants;

public class Session {
	private static Session INSTANCE;
	
	private Kml currentKML;
	private File currentSourceFile;
	private List<TPLocation> currentTrail;
	
	private Map<String, StretchType> stretchTypes;
	private Map<String, Integer> stretchTypesIdMap;
	
	private boolean openedFromFile = true;
	
	private Session(){
		currentTrail = new ArrayList<TPLocation>();
		
		DatabaseManager db = DatabaseManager.getInstance();
		stretchTypes = db.loadStretchTypes();
	
		if(stretchTypes == null)
			stretchTypes = new HashMap<String, StretchType>();
		
		if(stretchTypes.size() == 0){
			stretchTypes.put(TypeConstants.FIXED_TYPE_INVALID, new StretchType(TypeConstants.FIXED_TYPE_INVALID, "Inválido", Color.BLACK, BehaviorType.OTHER));
			stretchTypes.put(TypeConstants.FIXED_TYPE_TRAIL, new StretchType(TypeConstants.FIXED_TYPE_TRAIL, "Trilha", Color.GREEN, BehaviorType.OTHER));
			stretchTypes.put(TypeConstants.FIXED_TYPE_ROAD, new StretchType(TypeConstants.FIXED_TYPE_ROAD, "Estrada", Color.GRAY, BehaviorType.OTHER));
			stretchTypes.put(TypeConstants.FIXED_TYPE_RIVER, new StretchType(TypeConstants.FIXED_TYPE_RIVER, "Rio", Color.BLUE, BehaviorType.OTHER));
			stretchTypes.put(TypeConstants.FIXED_TYPE_SNOW, new StretchType(TypeConstants.FIXED_TYPE_SNOW, "Neve", new Color(230,230,230), BehaviorType.OTHER));
		}
		
		stretchTypesIdMap = new HashMap<String, Integer>();
		
		updateIdMap();
	}
	
	public static Session getInstance(){
		if(INSTANCE == null)
			INSTANCE = new Session();
		
		return INSTANCE;
	}
	
	public void updateIdMap(){
		int mappedIndexType = 0;
		stretchTypesIdMap.clear();
		
		for (Map.Entry<String, StretchType> entry : stretchTypes.entrySet()){
			stretchTypesIdMap.put(entry.getKey(), mappedIndexType);
		    mappedIndexType++;
		}
	}

	public Kml getCurrentKML() {
		return currentKML;
	}

	public void setCurrentKML(Kml currentKML) {
		this.currentKML = currentKML;
	}

	public File getCurrentSourceFile() {
		return currentSourceFile;
	}

	public void setCurrentSourceFile(File currentSourceFile) {
		this.currentSourceFile = currentSourceFile;
	}

	public List<TPLocation> getCurrentTrail() {
		return currentTrail;
	}

	public void setCurrentTrail(List<TPLocation> currentTrail) {
		this.currentTrail = currentTrail;
	}

	public Map<String, StretchType> getStretchTypes() {
		return stretchTypes;
	}

	public void setStretchTypes(Map<String, StretchType> stretchTypes) {
		this.stretchTypes = stretchTypes;
	}

	public Map<String, Integer> getStretchTypesIdMap() {
		return stretchTypesIdMap;
	}

	public void setStretchTypesIdMap(Map<String, Integer> stretchTypesIdMap) {
		this.stretchTypesIdMap = stretchTypesIdMap;
	}

	public boolean isOpenedFromFile() {
		return openedFromFile;
	}

	public void setOpenedFromFile(boolean openedFromFile) {
		this.openedFromFile = openedFromFile;
	}
}
