package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.LatLng;

import model.TPLocation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ElevationUtil {

	public static List<TPLocation> getElevationFromGoogle(List<TPLocation> locs) throws IOException{
			
	      try {
	    	String urlStr = "https://maps.googleapis.com/maps/api/elevation/json?locations=<locs>&key=<key>";
	    		
	    	StringBuilder sb = new StringBuilder();
	    	TPLocation loc;
	    	
	    	for(int i = 0; i < locs.size(); i++){
	    		loc = locs.get(i);
	    		sb.append(loc.getLatitude() + "," + loc.getLongitude() + "%7C");
	    	}
	    	
	    	String locsStr = sb.toString();
	    	locsStr = locsStr.substring(0, locsStr.length() - 1);
	    	
	    	urlStr.replace("<locs>", locsStr);
	    	urlStr.replace("<key>", "AIzaSyDDI0vYKqLBG6rL8-i8DadxFR23Odk84xU");
	    	
	    	URL url = new URL(urlStr);
	    	HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
	    	
	 	    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    	/*JSONObject obj = new JSONObject(response.body().toString());
	    	obj.
	    	
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
			}*/

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
