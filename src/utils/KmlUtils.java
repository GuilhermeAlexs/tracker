package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
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
		
		if(coords.length == 3)
			return new TPLocation(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]), Double.parseDouble(coords[2]));
		else
			return new TPLocation(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));

	}
	
	public static TPLocation coordinateToTPLocation(Coordinate c){
		return new TPLocation(c.getLatitude(), c.getLongitude(), c.getAltitude());
	}
	
	public static List<Coordinate> tpLocationsToCoordinates(List<TPLocation> locs){
		List<Coordinate> coords = new ArrayList<Coordinate>();
		
		for(TPLocation loc: locs){
			Coordinate coord = new Coordinate(loc.getLongitude(), loc.getLatitude(), loc.getAltitude());
			coords.add(coord);
		}
		
		return coords;		
	}
	
	public static List<TPLocation> parsePlacemark(Placemark p, KmlParseProgressListener listener) throws Exception{
		Geometry geometry = p.getGeometry();
		List<Object> coords = new ArrayList<Object>();
		Track track = null;
		
		if(geometry instanceof Track){
			track = (Track) p.getGeometry();
			coords.addAll(track.getCoord());
		}else if(geometry instanceof LineString){
			LineString line = (LineString) geometry;
			coords.addAll(line.getCoordinates());
		}else if(geometry instanceof MultiGeometry){
			MultiGeometry mGeo = (MultiGeometry) p.getGeometry();
			LineString line = (LineString) mGeo.getGeometry().get(0);
			coords.addAll(line.getCoordinates());
		}
		
		List<TPLocation> locs = new ArrayList<TPLocation>();
        TPLocation lastLoc = null;
		int i = 0;
		
		for(Object obj: coords){
			TPLocation loc;
			if(obj instanceof String)
				loc = stringToTPLocation((String) obj);
			else
				loc = coordinateToTPLocation((Coordinate) obj);
			
			if(lastLoc != null && GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude()) <= 10)
				continue;
			
			if(track != null)
				loc.setWhen(track.getWhen().get(i));
			
			loc.setId(i);
			
			locs.add(loc);
			i = i + 1;
			
			if(listener != null)
				listener.onParseProgress(i);
			
			lastLoc = loc;
		}
		
		if(!(geometry instanceof Track) && locs.get(0).getAltitude() <= 0){
			List<TPLocation> locsWithElev = ElevationUtil.getElevationFromGoogle(locs);
			LineString line = null;
			
			if(geometry instanceof LineString){
				line = (LineString) geometry;
			}else if(geometry instanceof MultiGeometry){
				MultiGeometry mGeo = (MultiGeometry) p.getGeometry();
				line = (LineString) mGeo.getGeometry().get(0);
			}
			
			line.setCoordinates(tpLocationsToCoordinates(locsWithElev));
			listener.onParseFinish(true);
			return locsWithElev;
		}
		listener.onParseFinish(false);
		return locs;
	}
	
	public static List<TPLocation> getAllPlacemarks(Kml kml, KmlParseProgressListener listener) throws Exception{
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
        
        listener.onParseFinish(false);
        
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
	
	public static void parseKml(Kml kml, KmlParseProgressListener listener){
		Document doc = (Document) kml.getFeature();
        List<Feature> listFeat = doc.getFeature();
        Iterator<Feature> it = listFeat.iterator();
        
		if(listener != null)
			listener.onPreParse(0);
        
        while(it.hasNext())
        	traverseKml(it.next(), listener);
        
        listener.onParseFinish(false);
	}
}
