package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import database.DatabaseManager;
import model.BehaviorType;
import model.LinearFunction;
import model.Statistics;
import model.TPLocation;
import model.TableOfSpeeds;
import model.TypeConstants;
import view.Session;

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

	private static int getIndexByInterval(double m, double step){
		return (int) Math.floor(((double)(m + 90))/((double)step));
	}

	public static TableOfSpeeds calculateTableOfSpeeds(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, double steps) throws ParseException{
		double dx, dh, dt, v, m;

		TPLocation lastLoc = path.get(0);
		
		double [][] matrix = new double[numberOfTypes][(int)(180/steps)];
		double [][] counts = new double[numberOfTypes][(int)(180/steps)];
		
		int index;
		boolean reset = false;
		
		int mappedIndexType = 0;
		
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
			
			mappedIndexType = idMap.get(lastLoc.getTypeId());
			
			matrix[mappedIndexType][index] = matrix[mappedIndexType][index] + v;
			counts[mappedIndexType][index]++;
			
			lastLoc = loc;
		}
		
		TableOfSpeeds table = new TableOfSpeeds(matrix, counts);
		
		return table;
	}
	
	public static TableOfSpeeds calculateTableOfSpeedsWithMedian(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, double steps) throws ParseException{
		double dx, dh, dt, v, m;

		TPLocation lastLoc = path.get(0);
		
		double [][] matrix = new double[numberOfTypes][(int)(180/steps)];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][(int)(180/steps)];
		
		int index;
		boolean reset = false;
		
		int mappedIndexType = 0;
		
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
			dt = (DateUtils.toCalendar(loc.getWhen()).getTimeInMillis() - DateUtils.toCalendar(lastLoc.getWhen()).getTimeInMillis())/(double)3600000;
			v = Math.abs(dx/dt);

			if(v < 0.2){
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

			try{
				mappedIndexType = idMap.get(lastLoc.getTypeId());
			}catch (Exception e){
				mappedIndexType = idMap.get(TypeConstants.FIXED_TYPE_TRAIL);
			}
			
			if(medianFinder[mappedIndexType][index] == null)
				medianFinder[mappedIndexType][index] = new MedianFinder();
			
			medianFinder[mappedIndexType][index].addNum(v);
			matrix[mappedIndexType][index] = medianFinder[mappedIndexType][index].findMedian();
			
			lastLoc = loc;
		}
		
		TableOfSpeeds table = new TableOfSpeeds(matrix, null);
		
		return table;
	}	

	public static double [][] getSimpleAverageSpeedMatrix(double steps) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		Session session = Session.getInstance();
		
		String [] names = db.getAllTrailsNames();
		
		if(names == null)
			return null;
		
		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfSpeeds speedTable = StatisticsUtil.calculateTableOfSpeeds(db.load(names[0]), stretchTypesIdMap, numberOfTypes, steps);
		TableOfSpeeds speedTable2;
		
		for(int i = 1; i < names.length; i++){
			speedTable2 = StatisticsUtil.calculateTableOfSpeeds(db.load(names[i]), stretchTypesIdMap, numberOfTypes, steps);
			speedTable.setCounts(MathOperation.sumMatrix(speedTable.getCounts(), speedTable2.getCounts()));
			speedTable.setSpeeds(MathOperation.sumMatrix(speedTable.getSpeeds(), speedTable2.getSpeeds()));
		}
		
		return MathOperation.divideMatrix(speedTable.getSpeeds(), speedTable.getCounts());
	}

	public static double [][] getMedianSpeedMatrix(double steps) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		String [] names = db.getAllTrailsNames();
		Session session = Session.getInstance();
		
		if(names == null)
			return null;
		
		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfSpeeds speedTable2;
		
		double [][] avgSpeedTable = new double[numberOfTypes][(int)(180/steps)];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][(int)(180/steps)];
		
		for(int i = 0; i < names.length; i++){
			speedTable2 = StatisticsUtil.calculateTableOfSpeedsWithMedian(db.load(names[i]), stretchTypesIdMap, numberOfTypes, steps);
			
			for(int type = 0; type < numberOfTypes; type++){
				for(int m = 0; m < (180/steps); m++){
					if(speedTable2.getSpeeds()[type][m] == 0)
						continue;
					
					if(medianFinder[type][m] == null)
						medianFinder[type][m] = new MedianFinder();
					
					medianFinder[type][m].addNum(speedTable2.getSpeeds()[type][m]);
					avgSpeedTable[type][m] = medianFinder[type][m].findMedian();
				}
			}
		}
		
		return avgSpeedTable;
	}
	
	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, double steps){
		return getListOfFunctionsWithLoess(avgSpeedTable, -1, steps);
	}
	
	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, int type, double steps){
		double speed;
		double inclination;

		Session session = Session.getInstance();
		UnivariateInterpolator interpolator = new LoessInterpolator();
		List<UnivariateFunction> listFunc = new ArrayList<UnivariateFunction>();
		int numberOfTypes = session.getStretchTypes().size();
		
		for(int i = 0; i < numberOfTypes; i++){
			if(type != -1 && i != type)
				continue;
			
			List<Double> inclinations = new ArrayList<Double>();
			List<Double> speeds = new ArrayList<Double>();

			for(int j = 0; j < avgSpeedTable[i].length; j++){
				speed = avgSpeedTable[i][j];

				if(speed == 0)
					continue;

				inclination = (j*steps) - 90;

				inclinations.add(inclination);
				speeds.add(speed);
			}

			if(inclinations.size() > 0){
				try{
					listFunc.add(interpolator.interpolate(inclinations.stream().mapToDouble(j -> j).toArray(), 
						speeds.stream().mapToDouble(j -> j).toArray()));
				}catch(Exception e){
					listFunc.add(null);
				}
			}else{
				listFunc.add(null);
			}
		}

		return listFunc;
	}

	public static List<UnivariateFunction> getListOfFunctionsWithPolynomialFitting(double [][] avgSpeedTable, double steps){
		Session session = Session.getInstance();
		double speed;
		double inclination;
		
		List<UnivariateFunction> listFunc = new ArrayList<UnivariateFunction>();
		
		String typeID = null;
		boolean makePrediction;
		int numberOfTypes = session.getStretchTypes().size();
		BehaviorType behaviorType;

		for(int i = 0; i < numberOfTypes; i++){
			typeID = getIdFromIndexType(i);
			behaviorType = session.getStretchTypes().get(typeID).getBehaviorType();
	
			if(behaviorType == BehaviorType.OTHER){
				listFunc.addAll(getListOfFunctionsWithLoess(avgSpeedTable, i, steps));
				continue;
			}
		
			WeightedObservedPoints obs = new WeightedObservedPoints();
			SimpleRegression linearObs = new SimpleRegression();
			makePrediction = false;

			for(int j = 0; j < avgSpeedTable[i].length; j++){
				speed = avgSpeedTable[i][j];

				if(speed == 0)
					continue;

				makePrediction = true;

				inclination = (j*steps) - 90;

				if(behaviorType == BehaviorType.LINEAR)
					linearObs.addData(inclination,speed);
				else
					obs.add(inclination,speed);
			}

			if(makePrediction){
				UnivariateFunction func;
				
				if(behaviorType == BehaviorType.LINEAR){
					func = new LinearFunction(linearObs);
				}else{
					PolynomialCurveFitter fitter;
					fitter = PolynomialCurveFitter.create(2);
					double[] coeff = fitter.fit(obs.toList());
					func = new PolynomialFunction(coeff);
				}
			
				listFunc.add(func);
			}else{
				listFunc.add(null);
			}
		}

		return listFunc;
	}
	
	private static String getIdFromIndexType(int index){
		Session session = Session.getInstance();
		for (Map.Entry<String, Integer> entry : session.getStretchTypesIdMap().entrySet()){
			if(entry.getValue() == index){
				return entry.getKey();
			}
		}

		return null;
	}
}
