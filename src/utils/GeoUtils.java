package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

import model.TPLocation; 

/**
 * Some geo-related utilities 
 * 
 * @author Martin Steiger 
 */ 
public class GeoUtils { 
 
    /**
     * WGS84 The flattening factor 1/f of the earth spheroid 
     */ 
    public static final double WGS84_EARTH_FLATTENING = 1.0 / 298.257223563; 
 
    /**
     * WGS84 The (transverse) major (equatorial) radius 
     */ 
    public static final double WGS84_EARTH_MAJOR = 6378137.0; 
 
    /**
     * WGS84 The polar semi-minor (conjugate) radius 
     */ 
    public static final double WGS84_EARTH_MINOR =  
            WGS84_EARTH_MAJOR * (1.0 - WGS84_EARTH_FLATTENING); 
 
    /**
     * The mean radius as defined by the International Union of Geodesy and 
     * Geophysics (IUGG) 
     */ 
    public static final double WGS84_MEAN_RADIUS =  
            (2 * WGS84_EARTH_MAJOR + WGS84_EARTH_MINOR) / 3.0; 
 
    /**
     * This uses the "haversine" formula to calculate the great-circle distance 
     * between two points � that is, the shortest distance over the earth's 
     * surface � giving an 'as-the-crow-flies' distance between the points 
     * 
     * @param lat1 latitude of point 1 
     * @param lon1 longitude of point 1 
     * @param lat2 latitude of point 2 
     * @param lon2 longitude of point 2 
     * @return distance in meters 
     */ 
    public static double computeDistance(double lat1, double lon1, double lat2, double lon2) { 
        double radius = 6371000; // 6371 kilometers == 3960 miles 
 
        double deltaLat = Math.toRadians(lat2 - lat1); 
        double deltaLon = Math.toRadians(lon2 - lon1); 
 
        // a is the square of half the chord length between the points 
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) 
                + Math.cos(Math.toRadians(lat1)) 
                * Math.cos(Math.toRadians(lat2)) * Math.sin(deltaLon / 2) 
                * Math.sin(deltaLon / 2); 
 
        // c is the angular distance in radians 
        double c = 2 * Math.asin(Math.min(1, Math.sqrt(a))); 
 
        return radius * c; 
    } 
 
    /**
     * @see GeoUtils#computeDistance(double, double, double, double) 
     * @param pos1 position 1 
     * @param pos2 position 2 
     * @return the distance in meters 
     */ 
    public static double computeDistance(GeoPosition pos1, GeoPosition pos2) { 
        return computeDistance( 
                pos1.getLatitude(), pos1.getLongitude(), 
                pos2.getLatitude(), pos2.getLongitude()); 
    }  
    
    public static List<TPLocation> smoothAltitude(List<TPLocation> path){
    	if(path.size() < 3)
    		return path;
    	
    	TPLocation before1,before2, curr, after1, after2;
    	double output;
    	
    	List<TPLocation> path2 = new ArrayList<TPLocation>();
    	TPLocation loc;
    	
    	for(int i = 2; i < path.size() - 3; i++){
    		before2 = path.get(i - 2);
    		before1 = path.get(i - 1);
    		curr = path.get(i);
    		after1 = path.get(i + 1);
    		after2 = path.get(i + 2);
    		
    		//if(GeoUtils.computeDistance(before.getLatitude(), before.getLongitude(), after.getLatitude(), after.getLongitude()) <= 30){
    			output = (before1.getAltitude() + after1.getAltitude())/(double)2;
    			//output = output + 0.4 * (curr.getAltitude() - output);
    		//}else{
    			//output = curr.getAltitude();
    		//}
    		
			loc = new TPLocation();
			loc.setId(curr.getId());
			loc.setLatitude(curr.getLatitude());
			loc.setLongitude(curr.getLongitude());
			loc.setSelected(curr.isSelected());
			loc.setTypeId(curr.getTypeId());
			loc.setWhen(curr.getWhen());
			loc.setAltitude(output);
			
			path2.add(loc);
    	}
    	
    	return path2;
    }

    public static List<TPLocation> interpolateWithGoogleData(List<TPLocation> path){
    	try {
    		List<TPLocation> googleData = ElevationUtil.getElevationFromGoogle(path.subList(0, path.size()/2));
			if(googleData == null)
				return path;
			
    		List<TPLocation> googleData2 = ElevationUtil.getElevationFromGoogle(path.subList(path.size()/2, path.size()));
			if(googleData2 == null)
				return path;
			
    		googleData.addAll(googleData2);
    		

			
			List<TPLocation> interpolated = new ArrayList<TPLocation>();
			TPLocation currLoc, googleLoc;
			
			for(int i = 0; i < path.size(); i++){
				TPLocation newLoc = new TPLocation();
				
				currLoc = path.get(i);
				googleLoc = googleData.get(i);
				
				newLoc.setId(currLoc.getId());
				newLoc.setLatitude(currLoc.getLatitude());
				newLoc.setLongitude(currLoc.getLongitude());
				
				
				newLoc.setAltitude((currLoc.getAltitude() + googleLoc.getAltitude()) / (double)2);
				
				
				newLoc.setTypeId(currLoc.getTypeId());
				newLoc.setWhen(currLoc.getWhen());
				newLoc.setSelected(currLoc.isSelected());
				
				interpolated.add(newLoc);
			}
			
			return interpolated;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return path;
    }
}