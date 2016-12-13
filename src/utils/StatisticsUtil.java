package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Statistics;
import model.TPLocation;
import model.TableOfSpeeds;
import model.TypeConstants;

public class StatisticsUtil {
	public static Statistics calculateStats(List<TPLocation> path){
		Statistics stats = new Statistics();
		List<Double> inclinations = new ArrayList<Double>();

		double maxElevation = -1;
		double minElevation = 9000;
		double avgElevation = 0;

		double elevationGain = 0;
		double elevationLoss = 0;

		double maxInclinationPositive = 0;
		double maxInclinationNegative = 0;
		double avgInclinationPositive = 0;
		double avgInclinationNegative = 0;

		double dx, dh, m;
		double length = 0;

		int elevationCount = 0;
		int inclinationPositiveCount = 0;
		int inclinationNegativeCount = 0;

		TPLocation lastLoc = path.get(0);
		boolean reset = false;
		
		for(TPLocation loc: path){
			if(loc.getTypeId() == TypeConstants.FIXED_TYPE_INVALID){
				reset = true;
				continue;
			}
			
			if(reset){
				reset = false;
				lastLoc = loc;
			}
			
			if(loc.getAltitude() > maxElevation)
				maxElevation = loc.getAltitude(); 

			if(loc.getAltitude() < minElevation)
				minElevation = loc.getAltitude(); 

			avgElevation = avgElevation + loc.getAltitude();
			elevationCount++;

			if(loc == lastLoc)
				continue;

			dx = GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude());
			dh = loc.getAltitude() - lastLoc.getAltitude();
			
			m = Math.toDegrees(Math.atan(dh/(double)dx));
			
			inclinations.add(m);

			if(m > 0){
				elevationGain = elevationGain + dh;
				avgInclinationPositive = avgInclinationPositive + m;
				inclinationPositiveCount++;

				if(m > maxInclinationPositive)
					maxInclinationPositive = m;
			}else{
				elevationLoss = elevationLoss + dh;
				avgInclinationNegative = avgInclinationNegative + m;
				inclinationNegativeCount++;

				if(m < maxInclinationNegative)
					maxInclinationNegative = m;
			}

			length = length + dx;

			lastLoc = loc;
		}

		stats.setElevationGain(elevationGain);
		stats.setElevationLoss(elevationLoss);
		stats.setLength(length);
		stats.setMaxElevation(maxElevation);
		stats.setMinElevation(minElevation);
		stats.setAvgElevation(avgElevation/elevationCount);
		stats.setInclinations(inclinations);
		stats.setAvgInclinationNegative(avgInclinationNegative/inclinationNegativeCount);
		stats.setAvgInclinationPositive(avgInclinationPositive/inclinationPositiveCount);
		stats.setMaxInclinationNegative(maxInclinationNegative);
		stats.setMaxInclinationPositive(maxInclinationPositive);

		return stats;
	}

	private static int getIndexByInterval(double m, int step){
		return (int) Math.floor(((double)(m + 90))/((double)step));
	}

	public static TableOfSpeeds calculateTableOfSpeeds(List<TPLocation> path, int steps) throws ParseException{
		double dx, dh, dt, v, m;

		TPLocation lastLoc = path.get(0);
		
		double [][] matrix = new double[5][180/steps];
		double [][] counts = new double[5][180/steps];
		int index;
		boolean reset = false;
		
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		int mappedIndexType;
		int currIndexTypeValue = 0;
		
		for(TPLocation loc: path){
			if(loc.getTypeId() == TypeConstants.FIXED_TYPE_INVALID){
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
			dt = (DateUtils.toCalendar(loc.getWhen()).getTimeInMillis() - DateUtils.toCalendar(lastLoc.getWhen()).getTimeInMillis())/(double)3600000;
			v = Math.abs(dx/dt);

			if(v < 0.3 ){
				continue;
			}
			
			if(v >= 10){
				continue;
			}

			m = Math.toDegrees(Math.atan(((double)dh)/((double)dx)));

			if( m > 90 )
				m = 90;
			else if( m < -90)
				m = -90;
			
			index = getIndexByInterval(m, steps);
		
			if(idMap.containsKey(lastLoc.getTypeId())){
				mappedIndexType = idMap.get(lastLoc.getTypeId());
			}else{
				mappedIndexType = currIndexTypeValue;
				idMap.put(lastLoc.getTypeId(), currIndexTypeValue);
				currIndexTypeValue++;
			}
			
			matrix[mappedIndexType][index] = matrix[mappedIndexType][index] + v;
			counts[mappedIndexType][index]++;

			lastLoc = loc;
		}
		
		TableOfSpeeds table = new TableOfSpeeds(matrix, counts);
		
		return table;
	}
}
