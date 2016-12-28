package utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.UnivariateFunction;

import model.TPLocation;
import model.TableOfValues;
import model.ToblerFunction;
import model.TypeConstants;

public class Predictor {
	public static double predictHikingTime(List<TPLocation> path, Map<String, Integer> idMap, List<UnivariateFunction> functions) throws ParseException, IOException{
		double dx, dh, m;

		if(path.get(0).getAltitude() <= 0)
			path = ElevationUtil.getElevationFromGoogle(path);
		else
			path = GeoUtils.smoothAltitude(path);
		
		TPLocation lastLoc = path.get(0);

		boolean reset = false;
		int mappedIndexType = 0;
		double time = 0;
		UnivariateFunction f;

		for(TPLocation loc: path){
			if(loc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID)){
				reset = true;
				continue;
			}

			if(reset){
				reset = false;
				lastLoc = loc;
			}

			if(loc == lastLoc)
				continue;

			dx = (GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude()))/1000;
			dh = (loc.getAltitude() - lastLoc.getAltitude())/1000;
			m = Math.toDegrees(Math.atan(((double)dh)/((double)dx)));

			if( m > 90 )
				m = 90;
			else if( m < -90)
				m = -90;

			try{
				mappedIndexType = idMap.get(lastLoc.getTypeId());
			}catch (Exception e){
				mappedIndexType = idMap.get(TypeConstants.FIXED_TYPE_TRAIL);
			}

			f = functions.get(mappedIndexType);

			if(f != null){
				if(f instanceof ToblerFunction)
					m = (double)dh/(double)dx;
				
				time = time + (dx/f.value(m));
			}

			lastLoc = loc;
		}

		return time;
	}

	public static double predictRestTime(List<TPLocation> path, TableOfValues avgTable) throws ParseException{
		double dt = (DateUtils.toCalendar(path.get(path.size() - 1).getWhen()).getTimeInMillis() - 
				DateUtils.toCalendar(path.get(0).getWhen()).getTimeInMillis())/(double)3600000;

		return avgTable.getRestProportion()*dt;
	}
}
