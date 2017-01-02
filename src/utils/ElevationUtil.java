package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import model.TPLocation;
import model.TypeConstants;

public class ElevationUtil {
	public static int MAX_LOCS_PER_REQUEST = 100;
	
	private static List<TPLocation> makeGoogleRequest(List<TPLocation> locs, int baseId) throws IOException{
	      try {
	    	String urlStr = "https://maps.googleapis.com/maps/api/elevation/json?path=<locs>&samples=<samples>&key=AIzaSyDDI0vYKqLBG6rL8-i8DadxFR23Odk84xU";

	    	StringBuilder sb = new StringBuilder();
	    	TPLocation loc;

	    	for(int i = 0; i < locs.size(); i++){
	    		loc = locs.get(i);
	    		sb.append(loc.getLatitude() + "%2C" + loc.getLongitude());

	    		if(i != locs.size() - 1)
	    			sb.append("%7C");
	    	}

	    	String locsStr = sb.toString();
	    	locsStr = locsStr.substring(0, locsStr.length() - 1);

	    	urlStr = urlStr.replace("<locs>", locsStr);
	    	urlStr = urlStr.replace("<samples>", "" + locs.size() * 2);

	    	URL url = new URL(urlStr);
	    	URLConnection con = (HttpsURLConnection)url.openConnection();
	    	con.setUseCaches(false);

	 	    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

	 	    String rawJson = "";
	 	    String line = "";

	 	    while(true){
	 	    	line = br.readLine();
	 	    	
	 	    	if(line == null)
	 	    		break;
	 	    	
	 	    	rawJson = rawJson + line;
	 	    }

	    	JSONObject json = new JSONObject(rawJson.toString());
	    	JSONArray arrJson = json.getJSONArray("results");
	    	List<TPLocation> locs2 = new ArrayList<TPLocation>();

			for(int i = 0; i < arrJson.length(); i++){
				TPLocation l = new TPLocation();
				l.setAltitude(arrJson.getJSONObject(i).getDouble("elevation"));
				l.setLatitude(arrJson.getJSONObject(i).getJSONObject("location").getDouble("lat"));
				l.setLongitude(arrJson.getJSONObject(i).getJSONObject("location").getDouble("lng"));
				l.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
				l.setId(baseId + i);
				locs2.add(l);
			}

			return locs2;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<TPLocation> getElevationFromGoogle(List<TPLocation> locs) throws IOException{
		List<TPLocation> locResult = new ArrayList<TPLocation>();
		System.out.println("Requisitando " + locs.size() + " do Elevation API.");

		if(locs.size() <= MAX_LOCS_PER_REQUEST)
			locResult = makeGoogleRequest(locs, 0);
		else{
			int i, qtd, remain;
			int baseId = 0;

			qtd = (int) Math.floor(locs.size() / MAX_LOCS_PER_REQUEST);
			remain = locs.size() % MAX_LOCS_PER_REQUEST;

			for(i = 0; i < qtd; i++){
				if(locResult.size() > 0)
					baseId = locResult.get(locResult.size() - 1).getId() + 1;

				locResult.addAll(makeGoogleRequest(locs.subList(i * MAX_LOCS_PER_REQUEST, (i + 1) * MAX_LOCS_PER_REQUEST), baseId));
			}

			if(remain > 0){
				if(locResult.size() > 0)
					baseId = locResult.get(locResult.size() - 1).getId() + 1;

				locResult.addAll(makeGoogleRequest(locs.subList(i * MAX_LOCS_PER_REQUEST, i * MAX_LOCS_PER_REQUEST + remain), baseId));
			}
		}

		return locResult;
	}
}
