package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;
import model.TPLocation;
import utils.listeners.KmlParseProgressListener;

public class KmlUtils {
	public static Placemark findByName(Kml kml, String name){
		Document doc = (Document) kml.getFeature();
        List<Feature> listFeat = doc.getFeature();
        Iterator<Feature> it = listFeat.iterator(); 
        
        while(it.hasNext()){
        	Feature feat = it.next();
        	if(feat instanceof Folder){
        		Folder folder = (Folder) feat;
        		Iterator<Feature> itFolder = folder.getFeature().iterator();
        		
        		while(itFolder.hasNext()){
        			Feature featFolder = itFolder.next();
        			if(featFolder instanceof Placemark){
        				Placemark p = (Placemark) featFolder;
        				if(p.getName().equals("name"))
        					return p;
        			}
        		}
        	}
        }
        
        return null;
	}
	
	public static TPLocation stringToTPLocation(String c){
		String [] coords = c.split(" ");
		return new TPLocation(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]), Double.parseDouble(coords[2]));
	}
	
	public static List<TPLocation> parsePlacemark(Placemark p, KmlParseProgressListener listener){
		Track track = (Track) p.getGeometry();
		List<String> coords = track.getCoord();
		List<TPLocation> locs = new ArrayList<TPLocation>();
        TPLocation lastLoc = null;
		int i = 0;
		
		for(String c: coords){
			TPLocation loc = stringToTPLocation(c);
			
			if(lastLoc != null && GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude()) <= 10)
				continue;
			
			loc.setWhen(track.getWhen().get(i));
			loc.setId(i);
			
			locs.add(loc);
			i = i + 1;
			
			if(listener != null)
				listener.onParseProgress(i);
			
			lastLoc = loc;
		}
		
		return locs;
	}
	
	public static List<TPLocation> getAllPlacemarks(Kml kml, KmlParseProgressListener listener){
		Document doc = (Document) kml.getFeature();
        List<Feature> listFeat = doc.getFeature();
        Iterator<Feature> it = listFeat.iterator(); 
        List<TPLocation> locs = new ArrayList<TPLocation>();
        
        while(it.hasNext()){
        	Feature feat = it.next();
        	if(feat instanceof Folder){
        		Folder folder = (Folder) feat;
        		
        		if(!folder.getName().equals("Tracks"))
        			continue;
        		
        		Iterator<Feature> itFolder = folder.getFeature().iterator();
    			
        		while(itFolder.hasNext()){
        			Feature featFolder = itFolder.next();
        			
        			if(featFolder instanceof Placemark){
        				locs = parsePlacemark((Placemark) featFolder, listener);
        			}
        		}
        	}
        }
        
        listener.onParseFinish();
        
        return locs;
	}
	
	private static void traverseKml(Feature feat, KmlParseProgressListener listener){
		if(feat instanceof Folder){
			Folder folder = (Folder) feat;
			listener.onParseFolder(folder);
			Iterator<Feature> it = folder.getFeature().iterator();

			while(it.hasNext())
				traverseKml(it.next(), listener);
		}else if(feat instanceof Placemark){
			listener.onParsePlacemark((Placemark) feat);
		}
		
		return;
	}
	
	public static List<TPLocation> parseKml(Kml kml, KmlParseProgressListener listener){
		Document doc = (Document) kml.getFeature();
        List<Feature> listFeat = doc.getFeature();
        Iterator<Feature> it = listFeat.iterator(); 
        List<TPLocation> locs = new ArrayList<TPLocation>();
        
		if(listener != null)
			listener.onPreParse(0);
        
        while(it.hasNext())
        	traverseKml(it.next(), listener);
        
        listener.onParseFinish();
        
        return locs;
	}
}
