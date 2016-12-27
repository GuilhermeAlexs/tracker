package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.LatLng;

import model.TPLocation;

public class ElevationUtil {	
	public static List<TPLocation> getElevationFromGoogle(List<TPLocation> locs) throws IOException{

	      try {
	    	
	    	LatLng [] locsReq = new LatLng[locs.size()];
	    	TPLocation currLoc;
	    	
	    	for(int i = 0; i < locs.size(); i++){
	    		currLoc = locs.get(i);
	    		locsReq[i] = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
	    	}
	    	
	    	GeoApiContext ctx = new GeoApiContext();
	    	ctx.setApiKey("AIzaSyDDI0vYKqLBG6rL8-i8DadxFR23Odk84xU");

	    	ElevationResult[] res = ElevationApi.getByPoints(ctx, locsReq).await();
			List<TPLocation> locs2 = new ArrayList<TPLocation>();

			for(int i = 0; i < res.length; i++){
				TPLocation l = new TPLocation();
				l.setAltitude(res[i].elevation);
				l.setLatitude(res[i].location.lat);
				l.setLongitude(res[i].location.lng);
				locs2.add(l);
			}

			return locs2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
