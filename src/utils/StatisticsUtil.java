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
import model.PredictorFunction;
import model.Statistics;
import model.TPLocation;
import model.TableOfValues;
import model.TypeConstants;
import view.Session;

public class StatisticsUtil {
	public static Statistics calculateStats(List<TPLocation> path){
		Statistics stats = new Statistics();
		List<Double> inclinations = new ArrayList<Double>();

		double maxElevation = -1;
		double minElevation = 9000;

		double elevationGain = 0;
		double elevationLoss = 0;

		double maxInclinationPositive = 0;
		double maxInclinationNegative = 0;

		double dx, dh, m;
		double length = 0;
		TPLocation lastLoc = path.get(0);
		boolean reset = false;

		MedianFinder medIncliPos = new MedianFinder();
		MedianFinder medIncliNeg = new MedianFinder();
		MedianFinder medElev = new MedianFinder();
		
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

			medElev.addNum(loc.getAltitude());

			if(loc == lastLoc)
				continue;

			dx = GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude());
			dh = loc.getAltitude() - lastLoc.getAltitude();

			m = Math.toDegrees(Math.atan(dh/(double)dx));

			inclinations.add(m);

			if(m > 0){
				elevationGain = elevationGain + dh;
				medIncliPos.addNum(m);

				if(m > maxInclinationPositive)
					maxInclinationPositive = m;
			}else{
				elevationLoss = elevationLoss + dh;
				medIncliNeg.addNum(m);

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
		stats.setAvgElevation(medElev.findMedian());
		stats.setInclinations(inclinations);
		stats.setAvgInclinationNegative(medIncliNeg.findMedian());
		stats.setAvgInclinationPositive(medIncliPos.findMedian());
		stats.setMaxInclinationNegative(maxInclinationNegative);
		stats.setMaxInclinationPositive(maxInclinationPositive);

		return stats;
	}

	private static int getIndexByInterval(double m, double step){
		return (int) Math.floor(((double)(m + 90))/((double)step));
	}

	public static TableOfValues calculateRestTimesWithMedian(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, double steps) throws ParseException{
		double dx, dt, v;

		TPLocation lastLoc = path.get(0);

		double [][] matrix = new double[numberOfTypes][(int)(180/steps)];

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

			dx = (GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude()))/(double)1000;
			dt = ((double)(DateUtils.toCalendar(loc.getWhen()).getTimeInMillis() - DateUtils.toCalendar(lastLoc.getWhen()).getTimeInMillis()))/(double)3600000;
			v = Math.abs(dx/dt);

			if(v > 0.2){
				lastLoc = loc;
				continue;
			}

			index = getIndexByInterval(0, steps);

			try{
				mappedIndexType = idMap.get(lastLoc.getTypeId());
			}catch (Exception e){
				mappedIndexType = idMap.get(TypeConstants.FIXED_TYPE_TRAIL);
			}

			matrix[mappedIndexType][index] = 1 + matrix[mappedIndexType][index];

			lastLoc = loc;
		}

		TableOfValues table = new TableOfValues(matrix, null);

		return table;
	}
	
	public static TableOfValues calculateTableOfSpeeds(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, double steps, double minSpeed, double maxSpeed) throws ParseException{
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

			if(v < minSpeed ){
				continue;
			}

			if(v >= maxSpeed){
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

		TableOfValues table = new TableOfValues(matrix, counts);

		return table;
	}
	
	public static TableOfValues calculateTableOfSpeedsWithMedian(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, double steps, double minSpeed, double maxSpeed) throws ParseException{
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

			if(v < minSpeed){
				lastLoc = loc;
				continue;
			}

			if(v >= maxSpeed){
				lastLoc = loc;
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

		TableOfValues table = new TableOfValues(matrix, null);

		return table;
	}
	

	public static double [][] getSimpleAverageSpeedMatrix(double steps, double minSpeed, double maxSpeed) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		Session session = Session.getInstance();

		String [] names = db.getAllTrailsNames();

		if(names == null)
			return null;

		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfValues speedTable = StatisticsUtil.calculateTableOfSpeeds(db.load(names[0]), stretchTypesIdMap, numberOfTypes, steps, minSpeed, maxSpeed);
		TableOfValues speedTable2;

		for(int i = 1; i < names.length; i++){
			speedTable2 = StatisticsUtil.calculateTableOfSpeeds(db.load(names[i]), stretchTypesIdMap, numberOfTypes, steps, minSpeed, maxSpeed);
			speedTable.setCounts(MathOperation.sumMatrix(speedTable.getCounts(), speedTable2.getCounts()));
			speedTable.setValues(MathOperation.sumMatrix(speedTable.getValues(), speedTable2.getValues()));
		}

		return MathOperation.divideMatrix(speedTable.getValues(), speedTable.getCounts());
	}

	public static double [][] getMedianSpeedMatrix(double steps, double minSpeed, double maxSpeed) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		String [] names = db.getAllTrailsNames();
		Session session = Session.getInstance();

		if(names == null)
			return null;

		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfValues speedTable2;

		double [][] avgSpeedTable = new double[numberOfTypes][(int)(180/steps)];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][(int)(180/steps)];

		for(int i = 0; i < names.length; i++){
			speedTable2 = StatisticsUtil.calculateTableOfSpeedsWithMedian(GeoUtils.interpolateWithGoogleData(db.load(names[i])), stretchTypesIdMap, numberOfTypes, steps, minSpeed, maxSpeed);

			for(int type = 0; type < numberOfTypes; type++){
				for(int m = 0; m < (180/steps); m++){
					if(speedTable2.getValues()[type][m] == 0)
						continue;

					if(medianFinder[type][m] == null)
						medianFinder[type][m] = new MedianFinder();

					medianFinder[type][m].addNum(speedTable2.getValues()[type][m]);
					avgSpeedTable[type][m] = medianFinder[type][m].findMedian();
				}
			}
		}

		return avgSpeedTable;
	}
	
	public static double [][] getMedianRestTimesMatrix(double steps) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		String [] names = db.getAllTrailsNames();
		Session session = Session.getInstance();

		if(names == null)
			return null;

		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfValues valueTable2;

		double [][] avgValueTable = new double[numberOfTypes][(int)(180/steps)];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][(int)(180/steps)];

		for(int i = 0; i < names.length; i++){
			valueTable2 = StatisticsUtil.calculateRestTimesWithMedian(db.load(names[i]), stretchTypesIdMap, numberOfTypes, steps);

			for(int type = 0; type < numberOfTypes; type++){
				for(int m = 0; m < (180/steps); m++){
					if(valueTable2.getValues()[type][m] == 0)
						continue;

					if(medianFinder[type][m] == null)
						medianFinder[type][m] = new MedianFinder();

					medianFinder[type][m].addNum(valueTable2.getValues()[type][m]);
					avgValueTable[type][m] = medianFinder[type][m].findMedian();
				}
			}
		}

		return avgValueTable;
	}
	
	
	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, double steps){
		return getListOfFunctionsWithLoess(avgSpeedTable, -1, steps);
	}
	
	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, int type, double steps){
		double value;
		double inclination;
		double maxIncli = -200;
		double minIncli = 200;

		Session session = Session.getInstance();
		UnivariateInterpolator interpolator = new LoessInterpolator();
		List<UnivariateFunction> listFunc = new ArrayList<UnivariateFunction>();
		int numberOfTypes = session.getStretchTypes().size();

		for(int i = 0; i < numberOfTypes; i++){
			if(type != -1 && i != type)
				continue;

			List<Double> inclinations = new ArrayList<Double>();
			List<Double> values = new ArrayList<Double>();

			for(int j = 0; j < avgSpeedTable[i].length; j++){
				value = avgSpeedTable[i][j];

				if(value == 0)
					continue;

				inclination = (j*steps) - 90;

				if(inclination > maxIncli)
					maxIncli = inclination;

				if(inclination < minIncli)
					minIncli = inclination;

				inclinations.add(inclination);
				values.add(value);
			}

			if(inclinations.size() > 0){
				try{
					UnivariateFunction predFunc = new PredictorFunction(interpolator.interpolate(inclinations.stream().mapToDouble(j -> j).toArray(), 
							values.stream().mapToDouble(j -> j).toArray()), minIncli, maxIncli);
					listFunc.add(predFunc);
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
