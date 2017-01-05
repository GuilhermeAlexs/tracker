package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import database.DatabaseManager;
import model.BehaviorType;
import model.Configurations;
import model.PredictorFunction;
import model.Statistics;
import model.Stretch;
import model.StretchIterator;
import model.TPLocation;
import model.TableOfValues;
import model.TypeConstants;
import model.regression.Regression;
import model.regression.RegressionFactory;
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

		double length = 0;

		double inclinationDegree;

		MedianFinder medIncliPos = new MedianFinder();
		MedianFinder medIncliNeg = new MedianFinder();
		MedianFinder medElev = new MedianFinder();

		StretchIterator it = new StretchIterator(path);
		Stretch stretch;

		while(it.hasNext()){
			stretch = it.next();

			if(stretch.getStart().getAltitude() > maxElevation)
				maxElevation = stretch.getStart().getAltitude(); 

			if(stretch.getStart().getAltitude() < minElevation)
				minElevation = stretch.getStart().getAltitude(); 

			medElev.addNum(stretch.getStart().getAltitude());

			inclinationDegree = Math.toDegrees(stretch.getTheta());
			inclinations.add(inclinationDegree);

			if(inclinationDegree > 0){
				elevationGain = elevationGain + stretch.getDiffAltitude();
				medIncliPos.addNum(inclinationDegree);

				if(inclinationDegree > maxInclinationPositive)
					maxInclinationPositive = inclinationDegree;
			}else{
				elevationLoss = elevationLoss + stretch.getDiffAltitude();
				medIncliNeg.addNum(inclinationDegree);

				if(inclinationDegree < maxInclinationNegative)
					maxInclinationNegative = inclinationDegree;
			}

			length = length + stretch.getDistance();
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

	public static TableOfValues calculateTableOfSpeedsWithMedian(List<TPLocation> path, Map<String, Integer> idMap, int numberOfTypes, Configurations conf) throws ParseException{	
		int sizeIncli = (int)(180/conf.getSteps());
		double [][] matrix = new double[numberOfTypes][sizeIncli];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][sizeIncli];

		int index;
		int mappedIndexType = 0;
		double dtAcc = 0;
		double dtTotal = 0;
		boolean canTakeRestInAccount = true;

		StretchIterator it = new StretchIterator(path);
		Stretch stretch;

		while(it.hasNext()){
			stretch = it.next();

			dtTotal = dtTotal + stretch.getTime();

			if(stretch.getSpeed() < conf.getMinimumSpeed()){
				if(stretch.getTime() < conf.getRestTime()){
					if(canTakeRestInAccount){
						canTakeRestInAccount = false;
						dtAcc = dtAcc + stretch.getTime();
					}
				}

				continue;
			}

			canTakeRestInAccount = true;

			if(stretch.getSpeed() >= conf.getMaximumSpeed())
				continue;

			index = getIndexByInterval(Math.toDegrees(stretch.getTheta()), 
					conf.getSteps());

			try{
				mappedIndexType = idMap.get(stretch.getStart().getTypeId());
			}catch (Exception e){
				mappedIndexType = idMap.get(TypeConstants.FIXED_TYPE_TRAIL);
			}

			if(medianFinder[mappedIndexType][index] == null)
				medianFinder[mappedIndexType][index] = new MedianFinder();

			medianFinder[mappedIndexType][index].addNum(stretch.getSpeed());
			matrix[mappedIndexType][index] = medianFinder[mappedIndexType][index].findMedian();
		}

		TableOfValues table = new TableOfValues(matrix, null, (double)dtAcc/(double)dtTotal);

		System.out.println("   Tempo Parado: " + 100*((double)dtAcc/(double)dtTotal) + "%");
		return table;
	}

	public static TableOfValues getMedianSpeedMatrix(Configurations conf) throws ParseException{
		DatabaseManager db = DatabaseManager.getInstance();
		String [] names = db.getAllTrailsNames();
		Session session = Session.getInstance();

		if(names == null)
			return null;

		Map<String, Integer> stretchTypesIdMap = session.getStretchTypesIdMap();
		int numberOfTypes = session.getStretchTypes().size();
		TableOfValues speedTable2;

		int sizeIncli = (int)(180/conf.getSteps());
		double [][] avgSpeedTable = new double[numberOfTypes][sizeIncli];
		MedianFinder [][] medianFinder = new MedianFinder[numberOfTypes][sizeIncli];
		MedianFinder restProportionMedian = new MedianFinder();
		
		for(int i = 0; i < names.length; i++){
			System.out.println(names[i] + ":");
			speedTable2 = StatisticsUtil.calculateTableOfSpeedsWithMedian(db.load(names[i]), stretchTypesIdMap, numberOfTypes, conf);
			restProportionMedian.addNum(speedTable2.getRestProportion());
			
			for(int type = 0; type < numberOfTypes; type++){
				for(int m = 0; m < sizeIncli; m++){
					if(speedTable2.getValues()[type][m] == 0)
						continue;

					if(medianFinder[type][m] == null)
						medianFinder[type][m] = new MedianFinder();

					medianFinder[type][m].addNum(speedTable2.getValues()[type][m]);
					avgSpeedTable[type][m] = medianFinder[type][m].findMedian();
				}
			}
		}
		
		TableOfValues table = new TableOfValues(avgSpeedTable, null, restProportionMedian.findMedian());

		return table;
	}

	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, Configurations conf){
		return getListOfFunctionsWithLoess(avgSpeedTable, -1, conf);
	}

	public static List<UnivariateFunction> getListOfFunctionsWithLoess(double [][] avgSpeedTable, int type, Configurations conf){
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

				inclination = (j*conf.getSteps()) - 90;

				if(inclination > maxIncli)
					maxIncli = inclination;

				if(inclination < minIncli)
					minIncli = inclination;

				inclinations.add(inclination);
				values.add(value);
			}

			if(inclinations.size() > 0){
				try{
					UnivariateFunction predFunc = new PredictorFunction(
							interpolator.interpolate(inclinations.stream().mapToDouble(j -> j).toArray(), 
							values.stream().mapToDouble(j -> j).toArray()), minIncli, maxIncli, 
							Session.getInstance().getStretchTypes().get(getIdFromIndexType(i)));
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


	public static List<UnivariateFunction> getListOfPredictionFunctions(double [][] avgSpeedTable, Configurations conf){
		Session session = Session.getInstance();
		double speed, inclination;

		List<UnivariateFunction> listFunc = new ArrayList<UnivariateFunction>();

		String typeID = null;
		int numberOfTypes = session.getStretchTypes().size();
		BehaviorType behaviorType;

		for(int i = 0; i < numberOfTypes; i++){
			typeID = getIdFromIndexType(i);
			behaviorType = session.getStretchTypes().get(typeID).getBehaviorType();

			if(behaviorType == BehaviorType.OTHER){
				listFunc.addAll(getListOfFunctionsWithLoess(avgSpeedTable, i, conf));
				continue;
			}

			Regression regression = null;

			for(int j = 0; j < avgSpeedTable[i].length; j++){
				speed = avgSpeedTable[i][j];

				if(speed == 0)
					continue;

				inclination = (j*conf.getSteps()) - 90;

				if(regression == null)
					regression = RegressionFactory.createRegression(behaviorType);

				regression.addObservation(inclination,speed);
			}

			listFunc.add(regression.getFunction());
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
