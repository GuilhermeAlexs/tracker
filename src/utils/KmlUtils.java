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
import model.TPLocation2;
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
	
	public static TPLocation2 stringToTPLocation(String c){
		String [] coords = c.split(" ");
		return new TPLocation2(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]), Double.parseDouble(coords[2]));
	}
	
	public static List<TPLocation2> getAllPlacemarks(Kml kml, KmlParseProgressListener listener){
		Document doc = (Document) kml.getFeature();
        List<Feature> listFeat = doc.getFeature();
        Iterator<Feature> it = listFeat.iterator(); 
        List<TPLocation2> locs = new ArrayList<TPLocation2>();
        TPLocation2 lastLoc = null;
        
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
        				Placemark p = (Placemark) featFolder;
        				Track track = (Track) p.getGeometry();
        				List<String> coords = track.getCoord();

                		if(listener != null)
                			listener.onPreParse(coords.size());

        				int i = 0;
        				for(String c: coords){
        					TPLocation2 loc = stringToTPLocation(c);
        					
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
        			}
        		}
        	}
        }
        
        listener.onParseFinish();
        
        return locs;
	}
}
