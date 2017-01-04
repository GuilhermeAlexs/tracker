package utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.UnivariateFunction;

import model.Configurations;
import model.Stretch;
import model.StretchIterator;
import model.TPLocation;
import model.TableOfValues;
import model.ToblerFunction;
import model.TypeConstants;

public class Predictor {
	private static double predictHikingTime(List<TPLocation> path, Map<String, Integer> idMap, List<UnivariateFunction> functions, boolean shouldSmooth, Configurations conf) throws ParseException, IOException{
		double m;

		if(path.get(0).getAltitude() <= 0)
			path = ElevationUtil.getElevationFromGoogle(path);
		else{
			if(shouldSmooth)
				path = GeoUtils.smoothAltitude(path);
		}

		int mappedIndexType = 0;
		double time = 0;
		UnivariateFunction f;

		StretchIterator it = new StretchIterator(path);
		Stretch stretch;

		while(it.hasNext()){
			stretch = it.next();

			try{
				mappedIndexType = idMap.get(stretch.getStart().getTypeId());
			}catch (Exception e){
				mappedIndexType = idMap.get(TypeConstants.FIXED_TYPE_TRAIL);
			}

			f = functions.get(mappedIndexType);

			if(f != null){
				if(f instanceof ToblerFunction)
					m = Math.tan(Math.toRadians(stretch.getInclination()));
				else
					m = Math.toDegrees(Math.atan(stretch.getInclination()));

				time = time + (stretch.getDistance()/f.value(m));
			}
		}

		return time;
	}

	public static double predict(List<TPLocation> path, TableOfValues avgTable, Map<String, Integer> idMap, List<UnivariateFunction> functions, boolean shouldSmooth, Configurations conf) throws ParseException, IOException{
		double hikingTime = predictHikingTime(path, idMap, functions, shouldSmooth, conf);
		return hikingTime + (avgTable.getRestProportion() * hikingTime);
	}
}