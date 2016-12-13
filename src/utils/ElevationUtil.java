package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import model.TPLocation2;

public class ElevationUtil {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String API_KEY = "AIzaSyDDI0vYKqLBG6rL8-i8DadxFR23Odk84xU";
	
	public static List<TPLocation2> getElevationFromGoogle(List<TPLocation2> locs) throws IOException{
		String urlBase = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
		
		StringBuilder sb = new StringBuilder();

		for(TPLocation2 loc: locs){
			sb.append(loc.getLatitude() + "," + loc.getLongitude() + URLEncoder.encode("|", "UTF-8"));	
		}
		
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		String urlStr = urlBase.concat(sb.toString()).concat("&key=" + API_KEY);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(urlStr);
		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response = client.execute(request);
			
		if(response.getStatusLine().getStatusCode() != 200)
			return null;
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String resp = "";
		while ((resp = rd.readLine()) != null) {
			result.append(resp);
		}
		
		JSONObject obj = new JSONObject(result.toString());
		
		if(!obj.getString("status").equals("OK"))
			return null;
		
		JSONObject obj2;
		JSONArray arr = obj.getJSONArray("results");
		List<TPLocation2> locs2 = new ArrayList<TPLocation2>();
		
		for(int i = 0; i < arr.length(); i++){
			obj2 = arr.getJSONObject(i);
			TPLocation2 l = new TPLocation2();
			l.setAltitude(obj2.getDouble("elevation"));
			l.setLatitude(obj2.getJSONObject("location").getDouble("lat"));
			l.setLongitude(obj2.getJSONObject("location").getDouble("lng"));
			locs2.add(l);
		}
		
		return locs2;
	}
}
